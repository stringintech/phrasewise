package com.stringintech.phrasewise.core;

public record Pitch(Spelling spelling, int octave) {
    public int getMidiPitch() {
        int basePitch = switch (spelling.name()) {
            case C -> 0;
            case D -> 2;
            case E -> 4;
            case F -> 5;
            case G -> 7;
            case A -> 9;
            case B -> 11;
        };

        int accidentalOffset = switch (spelling.accidental()) {
            case SHARP -> 1;
            case FLAT -> -1;
            case NATURAL -> 0;
        };

        return ((octave + 1) * 12) + basePitch + accidentalOffset;
    }

    public Spelling getSpelling() {
        return spelling;
    }

    public int getOctave() {
        return octave;
    }
}