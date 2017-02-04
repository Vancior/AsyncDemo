package com.vancior.noteparserdemo.util;

import android.util.Log;

import com.vancior.noteparserdemo.bean.Chord;
import com.vancior.noteparserdemo.bean.Note;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by H on 2017/1/14.
 */

public class NoteParser {

    private static String TAG = "NoteParser";

    public List<Chord> parse(InputStream is) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        MyHandler handler = new MyHandler();
        parser.parse(is, handler);
        return handler.getChords();
    }

    private class MyHandler extends DefaultHandler {

        private List<Chord> chords;
        private Note note;
        private Chord chord;
        private StringBuilder builder;
        private boolean isValid;
        private boolean isChord;
        private boolean isTie;
        private boolean isRight;

        public List<Chord> getChords() {
            return chords;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            chords = new ArrayList<>();
            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (localName.equals("measure")) {
                isRight = true;
            } else if (localName.equals("backup")) {
                isRight = false;
            } else if (localName.equals("note")) {
                note = new Note();
                isValid = true;
                isChord = false;
                isTie = false;
            } else if (localName.equals("rest")) {
                isValid = false;
            } else if (localName.equals("chord")) {
//                Log.d(TAG, "startElement: Chord");
                isChord = true;
            } else if (localName.equals("tie") && attributes.getValue("type").equals("stop")) {
                isTie = true;
            } else if (localName.equals("print") && attributes.getValue("new-page") != null
                    && attributes.getValue("new-page").equals("yes")) {
                if (chord != null)
                    chord.setNewPage(true);
            }
            builder.setLength(0);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if (localName.equals("step")) {
                note.setPitchStep(builder.toString().charAt(0));
            } else if (localName.equals("octave")) {
                note.setPitchOctave(Integer.parseInt(builder.toString()));
            } else if (localName.equals("duration")) {
                note.setDuration(Float.parseFloat(builder.toString()));
            } else if (localName.equals("note") && isValid && isRight) {
                if (isChord) {
                    if (!isTie) {
                        chord.addNote(note);
//                        Log.d(TAG, "endElement: " + note.getPitchStep());
                    }
                } else {
                    if (chord != null) {
                        Log.d(TAG, "endElement: " + chord.toString());
                        chords.add(chord);
                    }
                    chord = new Chord();
                    if (!isTie) {
                        chord.addNote(note);
                    }
                }
            }
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            if (chord != null) {
                Log.d(TAG, "endDocument: " + chord.toString());
                chords.add(chord);
            }
        }

    }

}
