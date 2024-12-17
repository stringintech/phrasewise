package com.stringintech.phrasewise.core;

public record Spelling(NoteName name, Accidental accidental) {
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