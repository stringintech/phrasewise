package com.stringintech.phrasewise.util;

import com.stringintech.phrasewise.core.Spelling;
import com.stringintech.phrasewise.legacy.util.LilypondNotationHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.stringintech.phrasewise.core.NoteName.*;
import static com.stringintech.phrasewise.core.Spelling.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LilypondNotationHelperTest {

    private static final int MIDI_C4 = 60;
    private static final int MIDI_AS4 = 70;
    private static final int MIDI_B4 = 71;

    @ParameterizedTest(name = "In key of {0}, pitch {1} should be {2}")
    @MethodSource("provideEnharmonicTestCases")
    void enharmonicSpellingInDifferentKeys(String keyName, int midiPitch, Spelling tonic, String expectedNotation) {
        String notation = LilypondNotationHelper.midiPitchToLilyPond(midiPitch, tonic);
        assertEquals(expectedNotation, notation,
                String.format("Incorrect spelling for MIDI pitch %d in key of %s", midiPitch, keyName));
    }

    private static Stream<Arguments> provideEnharmonicTestCases() {
        return Stream.of(
                Arguments.of("F major", MIDI_AS4, natural(F), "bes'"),
                Arguments.of("B major", MIDI_AS4, natural(B), "ais'"),
                Arguments.of("C major", MIDI_AS4, natural(C), "bes'"),
                Arguments.of("Bb major", MIDI_AS4, flat(B), "bes'"),
                Arguments.of("F# major", MIDI_AS4, sharp(F), "ais'"),
                Arguments.of("C major", MIDI_C4, natural(C), "c'"),
                Arguments.of("F major", MIDI_C4, natural(F), "c'"),
                Arguments.of("B major", MIDI_C4, natural(B), "c'"),
                Arguments.of("F major", MIDI_B4, natural(F), "b'"),
                Arguments.of("Bb major", MIDI_B4, flat(B), "b'")
        );
    }

    @Test
    @DisplayName("Different octaves should produce correct LilyPond marks")
    void octaveNotation() {
        var tonic = natural(C);
        assertEquals("c,,", LilypondNotationHelper.midiPitchToLilyPond(24, tonic));
        assertEquals("c,", LilypondNotationHelper.midiPitchToLilyPond(36, tonic));
        assertEquals("c", LilypondNotationHelper.midiPitchToLilyPond(48, tonic));
        assertEquals("c'", LilypondNotationHelper.midiPitchToLilyPond(60, tonic));
        assertEquals("c''", LilypondNotationHelper.midiPitchToLilyPond(72, tonic));
    }
}