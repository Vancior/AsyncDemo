package com.vancior.mergedemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.vancior.mergedemo.audio.TestThread;
import com.vancior.noteparserdemo.bean.Chord;
import com.vancior.noteparserdemo.util.NoteParser;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    private List<Chord> chordList;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text1);

        parseSheet();
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                textView.setText(msg.getData().getString("Note"));
            }
        };

        TestThread thread = new TestThread(handler, chordList);
        thread.setRunning(true);
        thread.start();
    }

    private void parseSheet() {
        try {
            //temporary: open xml in assets
            InputStream is = getAssets().open("forTest.xml");
            NoteParser parser = new NoteParser();
            chordList = parser.parse(is);
        } catch (Exception e) {
            Log.d(TAG, "parseSheet: ");
            e.printStackTrace();
        }
    }



}
