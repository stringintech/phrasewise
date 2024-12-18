package com.stringintech.phrasewise.legacy.util;


import com.stringintech.phrasewise.core.Key;
import com.stringintech.phrasewise.core.Pitch;
import com.stringintech.phrasewise.core.Spelling;

public class LilypondNotationHelper {
    public static String midiPitchToLilyPond(int midiPitch, Key key) {
        Pitch pitch = key.newPitch(midiPitch);
        return spellingToLilyPond(pitch.getSpelling()) +
                getOctaveMarks(pitch.getOctave());
    }

    private static String spellingToLilyPond(Spelling spelling) {
        String base = spelling.name().toString().toLowerCase();
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