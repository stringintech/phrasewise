package com.stringintech.phrasewise.util;

import com.stringintech.phrasewise.model.PitchSpelling;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converts between user-friendly note symbols and internal pitch spellings.
 */
public class NoteSymbol {
    private static final Map<String, PitchSpelling.Spelling> SYMBOL_TO_SPELLING = Map.ofEntries(
            Map.entry("C", PitchSpelling.Spelling.natural(PitchSpelling.NoteName.C)),
            Map.entry("C#", PitchSpelling.Spelling.sharp(PitchSpelling.NoteName.C)),
            Map.entry("Db", PitchSpelling.Spelling.flat(PitchSpelling.NoteName.D)),
            Map.entry("D", PitchSpelling.Spelling.natural(PitchSpelling.NoteName.D)),
            Map.entry("D#", PitchSpelling.Spelling.sharp(PitchSpelling.NoteName.D)),
            Map.entry("Eb", PitchSpelling.Spelling.flat(PitchSpelling.NoteName.E)),
            Map.entry("E", PitchSpelling.Spelling.natural(PitchSpelling.NoteName.E)),
            Map.entry("F", PitchSpelling.Spelling.natural(PitchSpelling.NoteName.F)),
            Map.entry("F#", PitchSpelling.Spelling.sharp(PitchSpelling.NoteName.F)),
            Map.entry("Gb", PitchSpelling.Spelling.flat(PitchSpelling.NoteName.G)),
            Map.entry("G", PitchSpelling.Spelling.natural(PitchSpelling.NoteName.G)),
            Map.entry("G#", PitchSpelling.Spelling.sharp(PitchSpelling.NoteName.G)),
            Map.entry("Ab", PitchSpelling.Spelling.flat(PitchSpelling.NoteName.A)),
            Map.entry("A", PitchSpelling.Spelling.natural(PitchSpelling.NoteName.A)),
            Map.entry("A#", PitchSpelling.Spelling.sharp(PitchSpelling.NoteName.A)),
            Map.entry("Bb", PitchSpelling.Spelling.flat(PitchSpelling.NoteName.B)),
            Map.entry("B", PitchSpelling.Spelling.natural(PitchSpelling.NoteName.B))
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

    public static List<PitchSpelling.Spelling> spellingsFromSymbols(List<String> symbols) {
        return symbols.stream()
                .map(NoteSymbol::toSpelling)
                .collect(Collectors.toList());
    }

    public static PitchSpelling.Spelling toSpelling(String symbol) {
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