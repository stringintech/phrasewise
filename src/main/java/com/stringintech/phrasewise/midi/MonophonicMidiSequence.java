package com.stringintech.phrasewise.midi;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.List;

public class MonophonicMidiSequence {
    private final List<MidiNote> notes;

    public MonophonicMidiSequence(Track track) {
        notes = new ArrayList<>();
        int currentNotePitch = -1;
        long currentNoteStart = -1;
        int currentVelocity = 0;
        int currentChannel = 0;

        for (int i = 0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            MidiMessage message = event.getMessage();

            if (message instanceof ShortMessage sm) {
                int command = sm.getCommand();
                int pitch = sm.getData1();
                int velocity = sm.getData2();
                int channel = sm.getChannel();

                if (command == ShortMessage.NOTE_ON && velocity > 0) {
                    if (currentNotePitch != -1) {
                        throw new IllegalStateException("Multiple notes playing simultaneously");
                    }
                    currentNotePitch = pitch;
                    currentNoteStart = event.getTick();
                    currentVelocity = velocity;
                    currentChannel = channel;
                } else if ((command == ShortMessage.NOTE_OFF) ||
                        (command == ShortMessage.NOTE_ON && velocity == 0)) {
                    if (currentNotePitch == pitch) {
                        notes.add(new MidiNote(
                                pitch,
                                currentNoteStart,
                                event.getTick() - currentNoteStart,
                                currentVelocity,
                                currentChannel
                        ));
                        currentNotePitch = -1;
                    }
                }
            }
        }
    }

    public List<MidiNote> getNotes() {
        return notes;
    }
}
