package com.stringintech.phrasewise.core;

public record Spelling(NoteName name, Accidental accidental) {

    static final int[] SHARP_KEYS = {7, 2, 9, 4, 11, 6, 1}; // G D A E B F# C#

    static final Spelling[] SHARPS = {
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

    static final Spelling[] FLATS = {
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

    public static Spelling natural(NoteName note) {
        return new Spelling(note, Accidental.NATURAL);
    }

    public static Spelling sharp(NoteName note) {
        return new Spelling(note, Accidental.SHARP);
    }

    public static Spelling flat(NoteName note) {
        return new Spelling(note, Accidental.FLAT);
    }
}