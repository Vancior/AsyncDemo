package com.vancior.mergedemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.vancior.mergedemo.audio.TestThread;
import com.vancior.noteparserdemo.bean.Chord;
import com.vancior.noteparserdemo.util.NoteParser;

import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    private List<Chord> chordList;
    private PDFView pdfView;
    private int currentPage;
    private int maxPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pdfView = (PDFView) findViewById(R.id.pdfView);
        displayPdf();
        parseSheet();

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (currentPage < maxPages - 1)
                    pdfView.jumpTo(currentPage + 1);
                else
                    Toast.makeText(MainActivity.this, "Already in the rear", Toast.LENGTH_SHORT).show();
            }
        };

        TestThread thread = new TestThread(handler, chordList);
        thread.setRunning(true);
        thread.start();
    }

    private void parseSheet() {
        try {
            //temporary: open xml in assets
            InputStream is = getAssets().open("fortest.xml");
            NoteParser parser = new NoteParser();
            chordList = parser.parse(is);
        } catch (Exception e) {
            Log.d(TAG, "parseSheet: ");
            e.printStackTrace();
        }
    }

    private void displayPdf() {
        //temporary: open pdf in assets
        pdfView.fromAsset("fortest.pdf")
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
