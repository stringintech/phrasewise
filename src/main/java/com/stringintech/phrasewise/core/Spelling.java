package com.stringintech.phrasewise.core;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record Spelling(NoteName name, Accidental accidental) {
    public int getBasePitch() {
        int accidentalOffset = switch (accidental()) {
            case SHARP -> 1;
            case FLAT -> -1;
            case NATURAL -> 0;
        };

        return accidentalOffset + switch (name()) {
            case C -> 0;
            case D -> 2;
            case E -> 4;
            case F -> 5;
            case G -> 7;
            case A -> 9;
            case B -> 11;
        };
    }

    private static final Map<String, Spelling> SYMBOL_TO_SPELLING = Map.ofEntries(
            Map.entry("C", Spelling.natural(NoteName.C)),
            Map.entry("C#", Spelling.sharp(NoteName.C)),
            Map.entry("Db", Spelling.flat(NoteName.D)),
            Map.entry("D", Spelling.natural(NoteName.D)),
            Map.entry("D#", Spelling.sharp(NoteName.D)),
            Map.entry("Eb", Spelling.flat(NoteName.E)),
            Map.entry("E", Spelling.natural(NoteName.E)),
            Map.entry("F", Spelling.natural(NoteName.F)),
            Map.entry("F#", Spelling.sharp(NoteName.F)),
            Map.entry("Gb", Spelling.flat(NoteName.G)),
            Map.entry("G", Spelling.natural(NoteName.G)),
            Map.entry("G#", Spelling.sharp(NoteName.G)),
            Map.entry("Ab", Spelling.flat(NoteName.A)),
            Map.entry("A", Spelling.natural(NoteName.A)),
            Map.entry("A#", Spelling.sharp(NoteName.A)),
            Map.entry("Bb", Spelling.flat(NoteName.B)),
            Map.entry("B", Spelling.natural(NoteName.B))
    );

    public static Spelling fromSymbol(String symbol) {
        if (!SYMBOL_TO_SPELLING.containsKey(symbol)) {
            throw new IllegalArgumentException("Invalid note symbol: " + symbol);
        }
        return SYMBOL_TO_SPELLING.get(symbol);
    }

    public static List<Spelling> listFromSymbols(List<String> symbols) {
        return symbols.stream()
                .map(Spelling::fromSymbol)
                .collect(Collectors.toList());
    }

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