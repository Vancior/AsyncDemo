package com.vancior.noteparserdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.vancior.noteparserdemo.bean.Note;
import com.vancior.noteparserdemo.util.NoteParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private TextView textView1;
    private PDFView pdfView;
    private Button buttonPrevious;
    private Button buttonNext;
    private List<Note> notes;

    private int currentPage;
    private int maxPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setAction();
        parseSheet();
        displayNotation();
        displayPdf();
    }

    private void initView() {
        textView1 = (TextView) findViewById(R.id.text1);
        pdfView = (PDFView) findViewById(R.id.pdfView);
        buttonPrevious = (Button) findViewById(R.id.buttonPrevious);
        buttonNext = (Button) findViewById(R.id.buttonNext);
    }

    private void setAction() {

        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage == 0)
                    Toast.makeText(MainActivity.this, "Already in the rear", Toast.LENGTH_SHORT).show();
                else
                    pdfView.jumpTo(currentPage - 1, false);
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage == maxPages-1)
                    Toast.makeText(MainActivity.this, "Already in the tail", Toast.LENGTH_SHORT).show();
                else
                    pdfView.jumpTo(currentPage + 1, false);
            }
        });
    }

    private void parseSheet() {
        try {
            //temporary: open xml in assets
            InputStream is = getAssets().open("test.xml");
            NoteParser parser = new NoteParser();
            notes = parser.parse(is);
        } catch (Exception e) {
            Log.d(TAG, "parseSheet: Exception!");
        }
    }

    private void displayNotation() {
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

    private void displayPdf() {
        //temporary: open pdf in assets
        pdfView.fromAsset("MySecretBase.pdf")
                .enableSwipe(false)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        currentPage = page;
                    }
                })
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        maxPages = nbPages;
                    }
                })
                .load();
        pdfView.loadPages();

    }

}
