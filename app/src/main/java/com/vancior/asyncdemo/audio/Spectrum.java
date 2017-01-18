package com.vancior.asyncdemo.audio;

/**
 * Created by H on 2016/12/7.
 */

import com.vancior.asyncdemo.audio.FFT;

/**
 * Contains a frequency-domain representation of a sampled input
 * signal. Stores the spectrum and provides methods for accessing
 * and manipulating the spectrum.
 *
 * @author Weston Pay
 *
 */
public class Spectrum {

    double[] spectrum;
    double[] samples;
    int sampleRate;

    /**
     * Creates a spectrum from the provided byte data and given sample rate.
     *
     * @param data           Non-interlaced array of sample data, such as that retrieved
     *                       from an audio stream.
     * @param sampleRateInHz Sample rate used to record sample data.
     */
    public Spectrum(byte[] data, int sampleRateInHz) {
        this.sampleRate = sampleRateInHz;
        buildSpectrum(data);
    }

    public double[] getSpectrum() {
        return spectrum.clone();
    }

    public double[] getSamples() {
        return samples.clone();
    }

    /**
     * Builds the frequency-domain spectrum from sampled audio data.
     *
     * @param data Non-interlaced byte array of sample data.
     */
    public void buildSpectrum(byte[] data) {
        // Create interlaced double array of complex numbers to hold sample data.
        samples = byteToDouble(data);

        // Apply window function to sample data.
        hanningWindow(samples);
//        hammingWindow(samples);

        // Build FFT.
        FFT fft = new FFT(samples.length / 2, -1);
        fft.transform(samples);

        // Build frequency spectrum from interlaced results data.
        spectrum = new double[samples.length / 2];
        for (int i = 0; i < samples.length; i += 2)
            spectrum[i / 2] = Math.sqrt(samples[i] * samples[i] + samples[i + 1] * samples[i + 1]);
    }

    /**
     * Converts byte array sample data into an interlaced array of doubles.
     * The double alternates real and imaginary values, imaginary values are
     * set to zero in this method.
     *
     * @param data Non-interlaced byte array of sample data.
     * @return Interlaced double array of sample data normalized to the
     * range [-1.0 .. +1.0].
     */
    private double[] byteToDouble(byte[] data) {
        double[] sample = new double[data.length];


        // Converts 16-bit big endian byte data into an interlaced array of doubles.
//		for (int i = 0; i < data.length; i+=2)
//			sample[i] = ((data[i] << 8) | (data[i+1] & 0xFF)) / 32768.0;

        // Converts 16-bit little endian byte data into an interlaced array of doubles.
        for (int i = 0; i < data.length; i += 2)
            sample[i] = ((data[i] & 0xFF) | (data[i + 1] << 8)) / 32768.0;

        return sample;
    }

    /**
     * Applies a Hanning window to the supplied sample data.
     *
     * @param samples Interlaced array of sample data.
     */
    public static void hanningWindow(double[] samples) {
        for (int i = 0; i < samples.length; i += 2) {
            double hanning = 0.5 - 0.5 * Math.cos((2 * Math.PI * (i / 2)) / (samples.length / 2) - 1);
            samples[i] *= hanning;
        }
    }

    public static void hammingWindow(double[] samples) {
        for (int i = 0; i < samples.length; i += 2) {
            double hamming = 0.54 - 0.46 * Math.cos((2 * Math.PI * (i / 2)) / (samples.length / 2) - 1);
            samples[i] *= hamming;
        }
    }
}
