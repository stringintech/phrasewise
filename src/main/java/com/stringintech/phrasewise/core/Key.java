package com.stringintech.phrasewise.core;

//TODO minor major how?
public class Key {
    private final Spelling tonic;

    public Key(Spelling tonic) {
        this.tonic = tonic;
    }

    public Pitch newPitch(int midiPitch) {
        int normalizedPitch = midiPitch % 12;
        Spelling spelling = newSpelling(normalizedPitch);
        int octave = midiPitch / 12 - 1;
        return new Pitch(spelling, octave);
    }

    private Spelling newSpelling(int normalizedMidiPitch) {
        if (normalizedMidiPitch < 0 || normalizedMidiPitch > 11) { //TODO assertion
            throw new IllegalArgumentException("Normalized midi pitch must be between 0 and 11");
        }
        return useSharpNotes() ?
                SHARP_SPELLINGS[normalizedMidiPitch] :
                FLAT_SPELLINGS[normalizedMidiPitch];
    }

    private boolean useSharpNotes() {
        for (Spelling sharpKey : KEYS_WITH_SHARP_SPELLINGS) { //TODO map?
            if (sharpKey.equals(tonic)) return true;
        }
        return false;
    }

    public Spelling getTonic() {
        return tonic;
    }

    static final Spelling[] KEYS_WITH_SHARP_SPELLINGS = {
            Spelling.natural(NoteName.G),
            Spelling.natural(NoteName.D),
            Spelling.natural(NoteName.A),
            Spelling.natural(NoteName.E),
            Spelling.natural(NoteName.B),
            Spelling.sharp(NoteName.F),
            Spelling.sharp(NoteName.C)
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
}