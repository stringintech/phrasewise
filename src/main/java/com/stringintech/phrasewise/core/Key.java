package com.stringintech.phrasewise.core;

public class Key {
    private final Spelling tonic;
    private final Mode mode;
    private final boolean useSharpNotes;

    public Key(Spelling tonic, Mode mode) {
        this.tonic = tonic;
        this.mode = mode;
        this.useSharpNotes = mode == Mode.MINOR ?
                isSharpMinorKey(tonic) :
                isSharpMajorKey(tonic);
    }

    public enum Mode {
        MAJOR, MINOR
    }

    public Pitch newPitch(int midiPitch) {
        int normalizedPitch = midiPitch % 12;
        Spelling spelling = newSpelling(normalizedPitch);
        int octave = midiPitch / 12 - 1;
        return new Pitch(spelling, octave);
    }

    private static final Spelling[] MAJOR_SHARP_KEYS = {
            Spelling.natural(NoteName.C),
            Spelling.natural(NoteName.G),
            Spelling.natural(NoteName.D),
            Spelling.natural(NoteName.A),
            Spelling.natural(NoteName.E),
            Spelling.natural(NoteName.B),
            Spelling.sharp(NoteName.F),
    };

    private static final Spelling[] MINOR_SHARP_KEYS = {
            Spelling.natural(NoteName.A),
            Spelling.natural(NoteName.E),
            Spelling.natural(NoteName.B),
            Spelling.sharp(NoteName.F),
            Spelling.sharp(NoteName.C),
            Spelling.sharp(NoteName.G),
    };

    static final Spelling[] SHARP_SPELLINGS = {
            Spelling.natural(NoteName.C),
            Spelling.sharp(NoteName.C),
            Spelling.natural(NoteName.D),
            Spelling.sharp(NoteName.D),
            Spelling.natural(NoteName.E),
            Spelling.natural(NoteName.F),
            Spelling.sharp(NoteName.F),
            Spelling.natural(NoteName.G),
            Spelling.sharp(NoteName.G),
            Spelling.natural(NoteName.A),
            Spelling.sharp(NoteName.A),
            Spelling.natural(NoteName.B)
    };

    static final Spelling[] FLAT_SPELLINGS = {
            Spelling.natural(NoteName.C),
            Spelling.flat(NoteName.D),
            Spelling.natural(NoteName.D),
            Spelling.flat(NoteName.E),
            Spelling.natural(NoteName.E),
            Spelling.natural(NoteName.F),
            Spelling.flat(NoteName.G),
            Spelling.natural(NoteName.G),
            Spelling.flat(NoteName.A),
            Spelling.natural(NoteName.A),
            Spelling.flat(NoteName.B),
            Spelling.natural(NoteName.B)
    };

    private static boolean isSharpMajorKey(Spelling tonic) {
        for (Spelling sharpKey : MAJOR_SHARP_KEYS) {
            if (sharpKey.equals(tonic)) return true;
        }
        return false;
    }

    private static boolean isSharpMinorKey(Spelling tonic) {
        for (Spelling sharpKey : MINOR_SHARP_KEYS) {
            if (sharpKey.equals(tonic)) return true;
        }
        return false;
    }

    private Spelling newSpelling(int normalizedMidiPitch) {
        if (normalizedMidiPitch < 0 || normalizedMidiPitch > 11) { //TODO assertion
            throw new IllegalArgumentException("Normalized midi pitch must be between 0 and 11");
        }
        return this.useSharpNotes ?
                SHARP_SPELLINGS[normalizedMidiPitch] :
                FLAT_SPELLINGS[normalizedMidiPitch];
    }

    public Spelling getTonic() {
        return tonic;
    }

    public Mode getMode() {
        return mode;
    }
}