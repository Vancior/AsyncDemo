package com.vancior.noteparserdemo.bean;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H on 2017/2/2.
 */

public class Chord {

    private static String TAG = "Chord";

    private List<Note> notes;

    public Chord() {
        notes = new ArrayList<>();
    }

    public void addNote(Note note) {
        notes.add(note);
//        Log.d(TAG, "addNote: " + note.getPitchStep());
    }

    public List<Note> getChord() {
        return notes;
    }

    @Override
    public String toString() {
        String result = "";
        for (Note i: notes) {
            result += String.valueOf(i.getPitchStep()) + i.getPitchOctave() + " ";
        }
        return result;
    }
}
