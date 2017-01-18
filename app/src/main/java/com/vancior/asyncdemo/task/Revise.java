package com.vancior.asyncdemo.task;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.vancior.asyncdemo.audio.Spectrum;

/**
 * Created by H on 2016/12/7.
 */

public class Revise extends Thread {

    public static double[] revise;
    private static double[] spectrumArray;

    @Override
    public void run() {
        Log.d("Attention", "YES!!!!!");
        int sampleRate = 22050;
        int bufferSize = 8192;
//        bufferSize = 1024;

        AudioRecord record;

        record = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        record.startRecording();

        byte[] buffer = new byte[bufferSize];
        revise = new double[bufferSize/2];

        for (int i = 0; i < 10; ++i) {
            int byteRead = record.read(buffer, 0, bufferSize);

            if (byteRead > 0) {

                Spectrum spectrum = new Spectrum(buffer, sampleRate);
                spectrumArray = spectrum.getSpectrum();

                for (int j = 0; j < spectrumArray.length; ++j)
                    revise[j] += spectrumArray[j];
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Log.d("catch", "interrupted");
            }
        }

        for (int i = 0; i < revise.length; ++i) {
            revise[i] /= 8;
        }

        for (int i = 0; i < revise.length; ++i) {
            System.out.println(revise[i]);
        }

        record.stop();
        record.release();
    }
}
