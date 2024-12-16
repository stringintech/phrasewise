package com.stringintech.phrasewise.model;

/**
 * Represents musical pitch spelling with key signature context.
 */
public class PitchSpelling {
    private final int midiPitch;
    private final int keyRoot;
    private final SpellingPreference preference;

    public enum SpellingPreference {
        SHARP, FLAT, KEY_BASED
    }

    public enum Accidental {
        SHARP, FLAT, NATURAL
    }

    public enum NoteName {
        C, D, E, F, G, A, B
    }

    public record Spelling(NoteName note, Accidental accidental) {
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

    private static final int[] SHARP_KEYS = {7, 2, 9, 4, 11, 6, 1}; // G D A E B F# C#
    private static final Spelling[] SHARP_SPELLINGS = {
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

    private static final Spelling[] FLAT_SPELLINGS = {
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

    private PitchSpelling(int midiPitch, int keyRoot, SpellingPreference preference) {
        this.midiPitch = midiPitch;
        this.keyRoot = keyRoot;
        this.preference = preference;
    }

    public static PitchSpelling of(int midiPitch) {
        return new PitchSpelling(midiPitch, 60, SpellingPreference.KEY_BASED);
    }

    public static PitchSpelling inKey(int midiPitch, int keyRoot) {
        return new PitchSpelling(midiPitch, keyRoot, SpellingPreference.KEY_BASED);
    }

    public static PitchSpelling withPreference(int midiPitch, SpellingPreference preference) {
        return new PitchSpelling(midiPitch, 60, preference);
    }

    public Spelling getSpelling() {
        int normalizedPitch = midiPitch % 12;
        boolean useSharpSpelling = determineSpellingType(keyRoot % 12);
        return useSharpSpelling ?
                SHARP_SPELLINGS[normalizedPitch] :
                FLAT_SPELLINGS[normalizedPitch];
    }

    public int getOctave() {
        return (midiPitch / 12) - 1;
    }

    public int getScaleDegree() {
        return ((midiPitch - keyRoot + 12) % 12) + 1;
    }

    public int getMidiPitch() {
        return midiPitch;
    }

    private boolean determineSpellingType(int rootPitch) {
        if (preference != SpellingPreference.KEY_BASED) {
            return preference == SpellingPreference.SHARP;
        }
        for (int sharpKey : SHARP_KEYS) {
            if (sharpKey == rootPitch) return true;
        }
        return false;
    }
}