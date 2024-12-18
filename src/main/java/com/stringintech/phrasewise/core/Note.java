package com.stringintech.phrasewise.core;

import com.stringintech.phrasewise.midi.MidiNote;

public class Note {
    private final Pitch pitch;
    private final Duration duration;

    public Note(MidiNote midiNote, Key key, int resolution) { //TODO okay to pass key?
        this.pitch = key.newPitch(midiNote.pitch());
        this.duration = calculateDuration(midiNote.duration(), resolution);
    }

    private static Duration calculateDuration(long ticks, int resolution) {
        if (resolution != 480) throw new IllegalArgumentException("Unsupported resolution"); //TODO

        if (ticks == 1920) return Duration.WHOLE;
        //TODO HALF?
        if (ticks == 720) return Duration.HALF_DOTTED;
        if (ticks == 480) return Duration.QUARTER;
        if (ticks == 360) return Duration.QUARTER_DOTTED;
        if (ticks == 240) return Duration.EIGHTH;
        if (ticks == 180) return Duration.EIGHTH_DOTTED;
        if (ticks == 120) return Duration.SIXTEENTH;
        if (ticks == 90) return Duration.SIXTEENTH_DOTTED;
        if (ticks == 60) return Duration.THIRTY_SECOND;

        throw new IllegalArgumentException("Unsupported duration ticks: " + ticks);
    }

    public Pitch getPitch() {
        return pitch;
    }

    public Duration getDuration() {
        return duration;
    }
}
