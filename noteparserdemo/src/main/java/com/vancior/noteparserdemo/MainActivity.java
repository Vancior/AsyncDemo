package com.vancior.noteparserdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.vancior.noteparserdemo.bean.Note;
import com.vancior.noteparserdemo.util.NoteParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private TextView textView1;
    private List<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        parseSheet();
        display();
    }

    private void initView() {
        textView1 = (TextView) findViewById(R.id.text1);
    }

    private void parseSheet() {
        try {
            InputStream is = getAssets().open("test.xml");
            NoteParser parser = new NoteParser();
            notes = parser.parse(is);
        } catch (Exception e) {
            Log.d(TAG, "parseSheet: Exception!");
        }
    }

    private void display() {
        if (notes == null) {
            Log.e(TAG, "display: notes is null");
            return;
        }

        String result = "";
        for (Note i : notes) {
            result += i.getPitchStep() + " ";
            result += i.getPitchOctave() + " ";
            result += i.getDuration() + " ";
        }
        textView1.setText(result);
    }

}
