package com.vancior.mergedemo.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.vancior.noteparserdemo.bean.Chord;
import com.vancior.noteparserdemo.bean.Note;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by H on 2016/12/1.
 */

public class TestThread extends Thread {

    private static String TAG = "TestThread";

    private static double[] temperament = new double[]{130.18, 138.59, 146.83, 155.56, 164.81, 174.61, 185.00, 196.00, 207.65, 220.00,
            233.08, 246.94, 261.63, 277.18, 293.66, 311.13, 329.63, 349.23, 369.99, 392.00, 415.30, 440.00, 466.16, 493.88, 523.25,
            554.37, 587.33, 622.25, 659.25, 698.46, 739.99, 783.99, 830.61, 880.00, 932.33, 987.77};

    private static String[] toneString = new String[]{"C3", "C3#", "D3", "D3#", "E3", "F3", "F3#", "G3", "G3#", "A3",
            "A3#", "B3", "C4", "C4#", "D4", "D4#", "E4", "F4", "F4#", "G4", "G4#", "A4", "A4#", "B4", "C5",
            "C5#", "D5", "D5#", "E5", "F5", "F5#", "G5", "G5#", "A5", "A5#", "B5"};

    private static Map<String, Integer> toneIndex = new HashMap<>();

    static {
        int itr = 0;
        for (String str : toneString) {
            toneIndex.put(str, itr++);
        }
    }

    private Handler handler;
    private boolean isRunning;
    private double step;
    private static int[] mSampleRates = new int[]{8000, 11025, 22050, 44100};
    private double[] spectrumArray;
    private List<Chord> chordList;
    private Chord currentChord;
    private Chord nextChord;
    private int current = 0;
    private int chordsLength;
    private boolean newPage;

    public TestThread(Handler handler, List<Chord> chordList) {
        this.handler = handler;
        this.chordList = chordList;
        chordsLength = this.chordList.size();
        newPage = false;
    }

    public void setRunning(boolean b) {
        this.isRunning = b;
    }

    @Override
    public void run() {
        int sampleRate = 22050;
        int bufferSize = 8192;

        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        record.startRecording();

        byte[] buffer = new byte[bufferSize];

        step = (double) sampleRate / bufferSize;
        int length;

        while (isRunning) {
            int byteRead = record.read(buffer, 0, bufferSize);

            if (byteRead > 0) {

                Spectrum spectrum = new Spectrum(buffer, sampleRate);
                spectrumArray = spectrum.getSpectrum();

                length = spectrumArray.length;

                if (current < chordsLength && testNotation()) {
                    if (newPage) {
                        String send = "OK";
                        Message message = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString("Note", send);
                        message.setData(bundle);
                        handler.sendMessage(message);
                        Log.d("Send", "succeed");

                        newPage = false;
                    }
                }

            }

        }

        record.stop();
        record.release();
    }

    private boolean testNotation() {
        boolean result = true;
        int left, right;
        List<Note> noteList;

        noteList = chordList.get(current).getChord();

        for (Note j : noteList) {
            String temp = "";
            temp += j.getPitchStep();
            temp += j.getPitchOctave();
            //use alter to get the right temperament
            left = (int) (temperament[toneIndex.get(temp) + j.getAlter()] / step);
            right = left + 1;
            int left2 = left - 1;
            if (spectrumArray[left2] < 50.0 && spectrumArray[left] < 50.0 && spectrumArray[right] < 50.0) {
                result = false;
            } else {
                String msg = (left-2)*step + ": " + spectrumArray[left-2] + " " + (left-1)*step + ": " + spectrumArray[left-1] +
                        " " + left*step + ": " + spectrumArray[left] + " " + (left+1)*step + ": " + spectrumArray[left+1] +
                        " " + (left+2)*step + ": " + spectrumArray[left+2];
                Log.d(TAG, "testNotation: " + msg);
            }
            if (!result)
                break;
            Log.d(TAG, "testNotation: " + left + " " + right);
        }
        if (result) {
            Log.d(TAG, "testNotation: " + chordList.get(current).toString());
            if (chordList.get(current).getNewPage())
                newPage = true;
            current++;
        }
        return result;
    }

    /**
     * Quadratic Interpolation of Peak Location
     *
     * <p>Provides a more accurate value for the peak based on the
     * best fit parabolic function.
     *
     * <p>α = spectrum[max-1]
     * <br>β = spectrum[max]
     * <br>γ = spectrum[max+1]
     *
     * <p>p = 0.5[(α - γ) / (α - 2β + γ)] = peak offset
     *
     * <p>k = max + p = interpolated peak location
     *
     * <p>Courtesy: <a href="https://ccrma.stanford.edu/~jos/sasp/Quadratic_Interpolation_Spectral_Peaks.html">
     * information source</a>.
     *
     * @param index The estimated peak value to base a quadratic interpolation on.
     * @return Float value that represents a more accurate peak index in a spectrum.
     */
    private double quadraticPeak(int index) {
        double alpha, beta, gamma, p, k;

        alpha = spectrumArray[index-1];
        beta = spectrumArray[index];
        gamma = spectrumArray[index+1];

        p = 0.5f * ((alpha - gamma) / (alpha - 2*beta + gamma));

        k = index + p;

        return k;
    }

}