package com.stringintech.phrasewise.midi;

import com.stringintech.phrasewise.core.Spelling;

public record MidiNote(int pitch, long startTick, long duration, int velocity, int channel) {
    public boolean isEnharmonicWith(Spelling spelling) {
        return spelling.getBasePitch() == pitch % 12; //TODO negative pitch?
    }
}