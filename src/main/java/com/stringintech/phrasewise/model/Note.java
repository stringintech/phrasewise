package com.stringintech.phrasewise.model;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.List;

public class Note {
    private final int pitch;        // MIDI note number (0-127)
    private final NoteName noteName;  // Replaced String with enum
    private final int octave;       // e.g. 4 for middle C
    private final long startTick;   // Start time in ticks
    private final long duration;    // Duration in ticks
    private final int velocity;     // Note velocity (0-127)
    private final int channel;      // MIDI channel (0-15)

    public Note(int pitch, long startTick, long duration, int velocity, int channel) {
        this.pitch = pitch;
        this.startTick = startTick;
        this.duration = duration;
        this.velocity = velocity;
        this.channel = channel;

        // Calculate note name and octave
        this.noteName = NoteName.values()[(pitch % 12)];
        this.octave = (pitch / 12) - 1; // MIDI note 60 = middle C (C4)
    }

    public static List<Note> createListFrom(Track track) {
        List<Note> notes = new ArrayList<>();
        int[] noteStartTicks = new int[128];  // For each possible MIDI note
        int[] noteVelocities = new int[128];  // Store velocity for each note
        int[] noteChannels = new int[128];    // Store channel for each note

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
                        notes.add(new Note(
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

    public int getPitch() {
        return pitch;
    }

    public NoteName getNoteName() {
        return noteName;
    }

    public int getOctave() {
        return octave;
    }

    public long getStartTick() {
        return startTick;
    }

    public long getDuration() {
        return duration;
    }

    public int getVelocity() {
        return velocity;
    }

    public int getChannel() {
        return channel;
    }
}