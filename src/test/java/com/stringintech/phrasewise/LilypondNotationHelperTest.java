package com.stringintech.phrasewise;

import com.stringintech.phrasewise.util.LilypondNotationHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LilypondNotationHelperTest {

    // Define MIDI pitch constants for commonly used notes
    private static final int MIDI_C4 = 60;  // Middle C
    private static final int MIDI_AS4 = 70; // A#4/Bb4 - enharmonic note that varies by key signature
    private static final int MIDI_B4 = 71;  // B4

    @ParameterizedTest(name = "In key of {0}, pitch {1} should be {2}")
    @MethodSource("provideEnharmonicTestCases")
    void enharmonicSpellingInDifferentKeys(String keyName, int midiPitch, int keyRoot, String expectedNotation) {
        String notation = LilypondNotationHelper.midiPitchToLilyPond(midiPitch, keyRoot);
        assertEquals(expectedNotation, notation,
                String.format("Incorrect spelling for MIDI pitch %d in key of %s", midiPitch, keyName));
    }

    private static Stream<Arguments> provideEnharmonicTestCases() {
        return Stream.of(
                // Test cases for the enharmonic note A#/Bb across different keys
                Arguments.of("F major", MIDI_AS4, 65, "bes'"),  // Flat-based key signature: spell as Bb
                Arguments.of("B major", MIDI_AS4, 71, "ais'"),  // Sharp-based key signature: spell as A#
                Arguments.of("C major", MIDI_AS4, 60, "bes'"),  // No sharps/flats: prefer flat spelling
                Arguments.of("Bb major", MIDI_AS4, 70, "bes'"), // Multiple flats: definitely use Bb
                Arguments.of("F# major", MIDI_AS4, 66, "ais'"), // Multiple sharps: definitely use A#

                // Test cases for non-enharmonic notes in various keys
                Arguments.of("C major", MIDI_C4, 60, "c'"),    // C is always spelled as C
                Arguments.of("F major", MIDI_C4, 65, "c'"),    // regardless of the key signature
                Arguments.of("B major", MIDI_C4, 71, "c'"),    // since it has no enharmonic equivalent
                Arguments.of("F major", MIDI_B4, 65, "b'"),    // B natural remains B natural
                Arguments.of("Bb major", MIDI_B4, 70, "b'")    // even in flat-based keys
        );
    }

    @Test
    @DisplayName("Different octaves should produce correct LilyPond marks")
    void octaveNotation() {
        int cMajorRoot = 60; // Using C major for all octave tests

        // Test octave markings using C across different registers
        assertEquals("c,,", LilypondNotationHelper.midiPitchToLilyPond(24, cMajorRoot), "C1 should have two commas");
        assertEquals("c,", LilypondNotationHelper.midiPitchToLilyPond(36, cMajorRoot), "C2 should have one comma");
        assertEquals("c", LilypondNotationHelper.midiPitchToLilyPond(48, cMajorRoot), "C3 should have no marks");
        assertEquals("c'", LilypondNotationHelper.midiPitchToLilyPond(60, cMajorRoot), "C4 should have one apostrophe");
        assertEquals("c''", LilypondNotationHelper.midiPitchToLilyPond(72, cMajorRoot), "C5 should have two apostrophes");
    }
}