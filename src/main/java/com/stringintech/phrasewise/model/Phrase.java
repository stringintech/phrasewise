package com.stringintech.phrasewise.model;

import java.util.List;

public class Phrase {
    private List<Note> notes;
    private String tonic; // Optional, can be null

    public Phrase(List<Note> notes, String tonic) {
        this.notes = notes;
        this.tonic = tonic;
    }

    public String getTonic() {
        return tonic;
    }

    public void setTonic(String tonic) {
        this.tonic = tonic;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}