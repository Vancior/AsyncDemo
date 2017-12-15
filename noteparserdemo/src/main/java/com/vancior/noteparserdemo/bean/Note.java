package com.vancior.noteparserdemo.bean;

/**
 * Created by H on 2017/1/14.
 */

public class Note {

    private char pitchStep;
    private int pitchOctave;
    private float duration;
    private int alter;

    public Note() { }

    public Note(char pitchStep, int pitchOctave, float duration) {
        this.pitchStep = pitchStep;
        this.pitchOctave = pitchOctave;
        this.duration = duration;
    }

    public void setPitchStep(char pitchStep) {
        this.pitchStep = pitchStep;
    }

    public void setPitchOctave(int pitchOctave) {
        this.pitchOctave = pitchOctave;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public void setAlter(int alter) {
        this.alter = alter;
    }

    public char getPitchStep() {
        return pitchStep;
    }

    public int getPitchOctave() {
        return pitchOctave;
    }

    public float getDuration() {
        return duration;
    }

    public int getAlter() {
        return alter;
    }
}
