package com.stringintech.phrasewise.legacy.model;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.List;

public record MidiNote(int pitch, long startTick, long duration, int velocity, int channel) {

    public static List<MidiNote> listFromTrack(Track track) {
        List<MidiNote> notes = new ArrayList<>();
        int[] noteStartTicks = new int[128];
        int[] noteVelocities = new int[128];
        int[] noteChannels = new int[128];

        // Initialize arrays
        for (int i = 0; i < 128; i++) {
            noteStartTicks[i] = -1;
        }

        // Process all events in the track
        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            MidiMessage message = event.getMessage();

            if (message instanceof ShortMessage sm) {
                int command = sm.getCommand();
                int pitch = sm.getData1();
                int velocity = sm.getData2();
                int channel = sm.getChannel();

                if (command == ShortMessage.NOTE_ON && velocity > 0) {
                    // Note started
                    noteStartTicks[pitch] = (int) event.getTick();
                    noteVelocities[pitch] = velocity;
                    noteChannels[pitch] = channel;
                } else if ((command == ShortMessage.NOTE_OFF) ||
                        (command == ShortMessage.NOTE_ON && velocity == 0)) {
                    // Note ended
                    if (noteStartTicks[pitch] != -1) {
                        notes.add(new MidiNote(
                                pitch,
                                noteStartTicks[pitch],
                                event.getTick() - noteStartTicks[pitch],
                                noteVelocities[pitch],
                                noteChannels[pitch]
                        ));
                        noteStartTicks[pitch] = -1;
                    }
                }
            }
        }
        return notes;
    }
}