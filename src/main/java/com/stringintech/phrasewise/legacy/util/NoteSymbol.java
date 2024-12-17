package com.stringintech.phrasewise.legacy.util;

import com.stringintech.phrasewise.core.NoteName;
import com.stringintech.phrasewise.core.Spelling;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NoteSymbol {
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

    private static final Map<String, Integer> KEY_ROOTS = Map.ofEntries(
            Map.entry("C", 60), Map.entry("C#", 61), Map.entry("Db", 61),
            Map.entry("D", 62), Map.entry("D#", 63), Map.entry("Eb", 63),
            Map.entry("E", 64),
            Map.entry("F", 65), Map.entry("F#", 66), Map.entry("Gb", 66),
            Map.entry("G", 67), Map.entry("G#", 68), Map.entry("Ab", 68),
            Map.entry("A", 69), Map.entry("A#", 70), Map.entry("Bb", 70),
            Map.entry("B", 71)
    );

    public static List<Spelling> spellingsFromSymbols(List<String> symbols) {
        return symbols.stream()
                .map(NoteSymbol::toSpelling)
                .collect(Collectors.toList());
    }

    public static Spelling toSpelling(String symbol) {
        if (!SYMBOL_TO_SPELLING.containsKey(symbol)) {
            throw new IllegalArgumentException("Invalid note symbol: " + symbol);
        }
        return SYMBOL_TO_SPELLING.get(symbol);
    }

    public static int getKeyRoot(String keySymbol) {
        if (!KEY_ROOTS.containsKey(keySymbol)) {
            throw new IllegalArgumentException("Invalid key: " + keySymbol);
        }
        return KEY_ROOTS.get(keySymbol);
    }
}