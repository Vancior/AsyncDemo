package com.vancior.asyncdemo.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import junit.framework.Test;

import static com.vancior.asyncdemo.task.Revise.revise;
import static com.vancior.asyncdemo.audio.UpdateUI.initLineChart;
import static com.vancior.asyncdemo.audio.UpdateUI.sound;

/**
 * Created by H on 2016/12/1.
 */

public class TestThread extends Thread {

    private static double[] temperament = new double[]{130.18, 138.59, 146.83, 155.56, 164.81, 174.61, 185.00, 196.00, 207.65, 220.00,
            233.08, 246.94, 261.63, 277.18, 293.66, 311.13, 329.63, 349.23, 369.99, 392.00, 415.30, 440.00, 466.16, 493.88};

    private static String[] toneString = new String[]{"C3", "C3#", "D3", "D3#", "E3", "F3", "F3#", "G3", "G3#", "A4",
            "B4", "C4", "C4#", "D4", "D4#", "E4", "F4", "F4#", "G4", "G4#", "A5", "B5"};

    private Handler handler;
    private boolean isRunning;
    private double step;
    private static int[] mSampleRates = new int[]{8000, 11025, 22050, 44100};
    private double[] spectrumArray;


    public TestThread(Handler handler) {
        this.handler = handler;
    }

    public void setRunning(boolean b) {
        this.isRunning = b;
    }

    @Override
    public void run() {
        int sampleRate = 44100;
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        bufferSize = 8192;
//        bufferSize = 1024;

        initLineChart();

        AudioRecord record;

//        for (int rate : mSampleRates) {
//            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
//                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
//                    try {
//                        Log.d("Try", "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
//                                + channelConfig);
//                        bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat) * 4;
//
//                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
//                            // check if we can instantiate and have a success
//                            record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);
//
//                            if (record.getState() == AudioRecord.STATE_INITIALIZED)
//                                Log.d("Success", "!");
//
//                            record.startRecording();
//
//                            record.stop();
//                            record.release();
//
//                        }
//                    } catch (Exception e) {
//                        Log.e("Exception", rate + "Exception, keep trying.",e);
//                    }
//                }
//            }
//        }

        record = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        record.startRecording();

        byte[] buffer = new byte[bufferSize];

        step = (double) sampleRate / bufferSize;
        int length;

        while (isRunning) {
            int byteRead = record.read(buffer, 0, bufferSize);

            if (byteRead > 0) {
//                Message message = handler.obtainMessage();
//                Bundle bundle = new Bundle();
//                bundle.putByte("Data", buffer[0]);
//                bundle.putInt("Int", bufferSize);
//                message.setData(bundle);
//                handler.sendMessage(message);
//                Log.d("bufferSize", bufferSize + "");

                Spectrum spectrum = new Spectrum(buffer, sampleRate);
                spectrumArray = spectrum.getSpectrum();

                length = spectrumArray.length;
                if (revise != null) {
                    for (int i = 0; i < length; ++i) {
                        spectrumArray[i] -= revise[i];
                        if (spectrumArray[i] < 0)
                            spectrumArray[i] = 0;
                    }
                }

                sound = spectrumArray;

//                String send = getNotation();
                String send = TestNotation();
                if (send != "") {
                    Message message = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("Note", send);
                    message.setData(bundle);
                    handler.sendMessage(message);
                    Log.d("Send", "succeed");
                }

                Thread update = new UpdateUI();
                UpdateUI.setStep(step);
                update.start();
            }

        }

        record.stop();
        record.release();
    }

    private String TestNotation() {
        String result = "";
        double left, middle, right;
        int i = (int) (temperament[0] / step), end = (int) (temperament[temperament.length - 1] / step + 1);
//        Log.d("Notation", "succeed");
        for (; i < end; ++i) {
            left = spectrumArray[i - 1];
            middle = spectrumArray[i];
            right = spectrumArray[i + 1];
            if (middle-left > 20 && middle-right > 20) {
                result = String.format("%.2f", i*step);
            }
        }
        if (result != "")
            Log.d("String", result);

        return result;
    }

    private String getNotation() {
        String result = "";
        double left, middle, right;
        int i = (int) (temperament[0] / step), end = (int) (temperament[temperament.length - 1] / step + 1);
//        Log.d("Notation", "succeed");
        for (; i < end; ++i) {
            left = spectrumArray[i - 1];
            middle = spectrumArray[i];
            right = spectrumArray[i + 1];
            if (middle-left > 30 && middle-right > 30) {
//                Log.d("Notation", left + " " + middle + " " + right + " " + (i * step));
                int length = temperament.length;
                double peak = quadraticPeak(i);
                for (int j = 0; j < length; ++j) {
                    if (Math.abs(peak * step - temperament[j]) < 4.0) {
                        result += toneString[j] + ".";
                    }

                    if (j >= 12) {
                        Log.d("Notation", left + " " + middle + " " + right + " " + (i * step));
                    }

                }
                break;
            }
        }
        if (result != "")
            Log.d("String", result);

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