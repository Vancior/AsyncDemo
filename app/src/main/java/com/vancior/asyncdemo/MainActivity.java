package com.vancior.asyncdemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vancior.asyncdemo.task.Revise;
import com.vancior.asyncdemo.audio.TestThread;

import lecho.lib.hellocharts.view.*;


public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private Button cancelButton;
    private Button resetButton;
    private ProgressBar progressBar;
    private TextView textView;
    private TextView textView2;
    private TextView textView3;
    private Handler handler;
    private TestThread t;
    public static LineChartView lineChartView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                String notation = message.getData().getString("Note");
                textView3.setText(notation);
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (t != null)
            t.setRunning(false);
    }

    public void initView() {
        startButton = (Button) findViewById(R.id.startButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        resetButton = (Button) findViewById(R.id.resetButton);
        startButton.setOnClickListener(new ButtonOnClickListener());
        cancelButton.setOnClickListener(new ButtonOnClickListener());
        resetButton.setOnClickListener(new ButtonOnClickListener());
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        textView = (TextView) findViewById(R.id.displayBuff);
//        textView2 = (TextView) findViewById(R.id.displayZero);
        textView3 = (TextView) findViewById(R.id.displayNotation);
        lineChartView = (LineChartView) findViewById(R.id.chart);

        lineChartView.setInteractive(false);
        lineChartView.setVisibility(View.VISIBLE);
    }

    class ButtonOnClickListener implements View.OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.startButton:
                    if (t == null || !t.isAlive()) {
                        t = new TestThread(handler);
                        t.setRunning(true);
                        t.start();
                    }
                    break;
                case R.id.cancelButton:
                    Log.d("Click", "Clicked cancel!");
                    if (t != null)
                        t.setRunning(false);
                    break;
                case R.id.resetButton:
                    Log.d("Click", "Clicked!");
                    Thread r = new Revise();
                    r.start();
                    break;
                default:
                    break;
            }
        }
    }


}
