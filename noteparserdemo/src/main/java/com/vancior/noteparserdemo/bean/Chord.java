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
    private boolean newPage = false;

    public Chord() {
        notes = new ArrayList<>();
    }

    public void addNote(Note note) {
        notes.add(note);
//        Log.d(TAG, "addNote: " + note.getPitchStep());
    }

    public void setNewPage(boolean newPage) {
        this.newPage = newPage;
    }

    public boolean getNewPage() {
        return newPage;
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
        if (newPage)
            result += "new-page = yes";
        return result;
    }
}
