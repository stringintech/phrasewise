package com.stringintech.phrasewise.core;

import com.stringintech.phrasewise.midi.MonophonicMidiSequence;

import java.util.List;

public class MonophonicSequence {
    private final List<Note> notes;

    public MonophonicSequence(MonophonicMidiSequence midiSequence, Key key) {
        this.notes = midiSequence.getNotes().stream()
                .map(note -> new Note(note, key, midiSequence.getResolution()))
                .toList();
    }

    public List<Note> getNotes() {
        return notes;
    }
}
