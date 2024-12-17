package com.stringintech.phrasewise.core;

public class Note {
    private final Spelling spelling;
    private final Duration duration;
    private final int octave;

    public Note(Spelling spelling, Duration duration, int octave) {
        this.spelling = spelling;
        this.duration = duration;
        this.octave = octave;
    }

    public Spelling getSpelling() {
        return spelling;
    }

    public Duration getDuration() {
        return duration;
    }

    public int getOctave() {
        return octave;
    }
}
