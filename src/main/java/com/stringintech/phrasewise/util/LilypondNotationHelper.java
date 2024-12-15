package com.stringintech.phrasewise.util;

public class LilypondNotationHelper {
    // Key signatures organized by number of sharps/flats
    private static final int[] CIRCLE_OF_FIFTHS = {
            0,  // C  - no accidentals
            7,  // G  - 1 sharp (F#)
            2,  // D  - 2 sharps (F#, C#)
            9,  // A  - 3 sharps (F#, C#, G#)
            4,  // E  - 4 sharps (F#, C#, G#, D#)
            11, // B  - 5 sharps (F#, C#, G#, D#, A#)
            6,  // F# - 6 sharps (F#, C#, G#, D#, A#, E#)
            1,  // C# - 7 sharps (F#, C#, G#, D#, A#, E#, B#)
            5,  // F  - 1 flat  (Bb)
            10, // Bb - 2 flats (Bb, Eb)
            3,  // Eb - 3 flats (Bb, Eb, Ab)
            8,  // Ab - 4 flats (Bb, Eb, Ab, Db)
            1,  // Db - 5 flats (Bb, Eb, Ab, Db, Gb)
            6,  // Gb - 6 flats (Bb, Eb, Ab, Db, Gb, Cb)
            11  // Cb - 7 flats (Bb, Eb, Ab, Db, Gb, Cb, Fb)
    };

    // Sharp key signatures in circle of fifths order
    private static final int[] SHARP_KEYS = {7, 2, 9, 4, 11, 6, 1}; // G D A E B F# C#
    private static final String[] SHARP_NOTE_NAMES = {"c", "cis", "d", "dis", "e", "f", "fis", "g", "gis", "a", "ais", "b"};

    // Flat key signatures in circle of fifths order
    private static final int[] FLAT_KEYS = {5, 10, 3, 8, 1, 6, 11}; // F Bb Eb Ab Db Gb Cb
    private static final String[] FLAT_NOTE_NAMES = {"c", "des", "d", "ees", "e", "f", "ges", "g", "aes", "a", "bes", "b"};


    /**
     * Converts a MIDI pitch number to its correct LilyPond notation based on the key signature.
     *
     * @param midiPitch The MIDI pitch number (0-127)
     * @param rootPitch The MIDI pitch number of the scale's root note
     * @return The LilyPond notation for the note
     */
    public static String midiPitchToLilyPond(int midiPitch, int rootPitch) {
        // Normalize pitches to 0-11 range
        int normalizedPitch = midiPitch % 12;
        int normalizedRoot = rootPitch % 12;

        // Determine if we're in a sharp or flat key
        boolean usesSharps = shouldUseSharpSpelling(normalizedRoot);

        // Get base note name and accidental
        String[] noteAndAccidental = getNoteAndAccidental(normalizedPitch, usesSharps);
        String noteName = noteAndAccidental[0];

        // Calculate octave
        int octave = (midiPitch / 12) - 1;  // MIDI note 60 = middle C (C4)
        String octaveMarks = getOctaveMarks(octave);

        return noteName + octaveMarks;
    }

    /**
     * Determines whether to use sharp or flat spelling based on the key signature.
     */
    private static boolean shouldUseSharpSpelling(int rootPitch) {
        // Check if the root is in the sharp keys
        for (int sharpKey : SHARP_KEYS) {
            if (sharpKey == rootPitch) return true;
        }

        // Default to flats for C major and flat keys
        return false;
    }

    /**
     * Gets the proper note name and accidental based on the key signature context.
     */
    private static String[] getNoteAndAccidental(int pitch, boolean usesSharps) {
        String noteName = usesSharps ? SHARP_NOTE_NAMES[pitch] : FLAT_NOTE_NAMES[pitch];
        return new String[]{noteName, ""};
    }

    /**
     * Gets the proper octave marks for LilyPond notation.
     */
    private static String getOctaveMarks(int octave) {
        if (octave > 3) {
            return "'".repeat(octave - 3);
        } else if (octave < 3) {
            return ",".repeat(3 - octave);
        }
        return "";
    }
}