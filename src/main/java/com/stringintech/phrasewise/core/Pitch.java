package com.stringintech.phrasewise.core;

public record Pitch(Spelling spelling, int octave) {
    public int getMidiPitch() {
        return ((octave + 1) * 12) + spelling.getBasePitch();
    }

    public Spelling getSpelling() {
        return spelling;
    }

    public int getOctave() {
        return octave;
    }
}