package com.stringintech.phrasewise.util;

import com.stringintech.phrasewise.core.Key;
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
    void enharmonicSpellingInDifferentKeys(String keyName, int midiPitch, Key key, String expectedNotation) {
        String notation = LilypondNotationHelper.midiPitchToLilyPond(midiPitch, key);
        assertEquals(expectedNotation, notation,
                String.format("Incorrect spelling for MIDI pitch %d in key of %s", midiPitch, keyName));
    }

    private static Stream<Arguments> provideEnharmonicTestCases() {
        return Stream.of(
                Arguments.of("F major", MIDI_AS4, new Key(natural(F), Key.Mode.MAJOR), "bes'"),
                Arguments.of("B major", MIDI_AS4, new Key(natural(B), Key.Mode.MAJOR), "ais'"),
                Arguments.of("C major", MIDI_AS4, new Key(natural(C), Key.Mode.MAJOR), "ais'"),
                Arguments.of("Bb major", MIDI_AS4, new Key(flat(B), Key.Mode.MAJOR), "bes'"),
                Arguments.of("F# major", MIDI_AS4, new Key(sharp(F), Key.Mode.MAJOR), "ais'"),
                Arguments.of("C major", MIDI_C4, new Key(natural(C), Key.Mode.MAJOR), "c'"),
                Arguments.of("F major", MIDI_C4, new Key(natural(F), Key.Mode.MAJOR), "c'"),
                Arguments.of("B major", MIDI_C4, new Key(natural(B), Key.Mode.MAJOR), "c'"),
                Arguments.of("F major", MIDI_B4, new Key(natural(F), Key.Mode.MAJOR), "b'"),
                Arguments.of("Bb major", MIDI_B4, new Key(flat(B), Key.Mode.MAJOR), "b'")
        );
    }

    @Test
    @DisplayName("Different octaves should produce correct LilyPond marks")
    void octaveNotation() {
        var key = new Key(natural(C), Key.Mode.MAJOR);
        assertEquals("c,,", LilypondNotationHelper.midiPitchToLilyPond(24, key));
        assertEquals("c,", LilypondNotationHelper.midiPitchToLilyPond(36, key));
        assertEquals("c", LilypondNotationHelper.midiPitchToLilyPond(48, key));
        assertEquals("c'", LilypondNotationHelper.midiPitchToLilyPond(60, key));
        assertEquals("c''", LilypondNotationHelper.midiPitchToLilyPond(72, key));
    }
}