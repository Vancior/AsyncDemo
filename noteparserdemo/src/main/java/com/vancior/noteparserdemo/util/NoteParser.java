package com.vancior.noteparserdemo.util;

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

    public List<Note> parse(InputStream is) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        MyHandler handler = new MyHandler();
        parser.parse(is, handler);
        return handler.getNotes();
    }

    private class MyHandler extends DefaultHandler {

        private List<Note> notes;
        private Note note;
        private StringBuilder builder;
        private boolean isValid;

        public List<Note> getNotes() {
            return notes;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            notes = new ArrayList<>();
            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (localName.equals("note")) {
                note = new Note();
                isValid = true;
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
            if (localName.equals("rest")) {
                isValid = false;
            } else if (localName.equals("step")) {
                note.setPitchStep(builder.toString().charAt(0));
            } else if (localName.equals("octave")) {
                note.setPitchOctave(Integer.parseInt(builder.toString()));
            } else if (localName.equals("duration")) {
                note.setDuration(Float.parseFloat(builder.toString()));
            } else if (localName.equals("note") && isValid) {
                notes.add(note);
            }
        }

    }

}
