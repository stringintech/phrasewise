package com.stringintech.phrasewise.util;


import com.stringintech.phrasewise.model.PitchSpelling;

/**
 * Converts pitch spellings to LilyPond notation format.
 */
public class LilypondNotationHelper {
    static String midiPitchToLilyPond(int midiPitch, int rootPitch) {
        PitchSpelling pitchSpelling = PitchSpelling.inKey(midiPitch, rootPitch);
        return spellingToLilyPond(pitchSpelling.getSpelling()) +
                getOctaveMarks(pitchSpelling.getOctave());
    }

    private static String spellingToLilyPond(PitchSpelling.Spelling spelling) {
        String base = spelling.note().toString().toLowerCase();
        return base + switch (spelling.accidental()) {
            case SHARP -> "is";
            case FLAT -> "es";
            case NATURAL -> "";
        };
    }

    private static String getOctaveMarks(int octave) {
        if (octave > 3) return "'".repeat(octave - 3);
        if (octave < 3) return ",".repeat(3 - octave);
        return "";
    }
}