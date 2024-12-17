package com.stringintech.phrasewise.core;

public class PitchSpelling {
    private final Spelling spelling;
    private final int octave;

    public PitchSpelling(int midiPitch, int tonicMidiPitch) {
        boolean useSharpSpelling = shouldUserSharpSpelling(tonicMidiPitch % 12);
        int normalizedPitch = midiPitch % 12;
        this.spelling = useSharpSpelling ?
                Spelling.SHARPS[normalizedPitch] :
                Spelling.FLATS[normalizedPitch];
        this.octave = midiPitch / 12 - 1;
    }

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

    private static boolean shouldUserSharpSpelling(int tonicNormalizedPitch) {
        for (int sharpKey : Spelling.SHARP_KEYS) {
            if (sharpKey == tonicNormalizedPitch) return true;
        }
        return false;
    }

    public Spelling getSpelling() {
        return spelling;
    }

    public int getOctave() {
        return octave;
    }
}