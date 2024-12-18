package com.stringintech.phrasewise.midi;

import com.stringintech.phrasewise.core.Spelling;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MonophonicMidiSequence {
    private final List<MidiNote> notes;
    private final int resolution;

    public record NoteSequenceMatch(List<MidiNote> sequence, long startTick) {
    }

    public MonophonicMidiSequence(Track track, int resolution) {
        this.resolution = resolution;
        this.notes = new ArrayList<>();
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
                        this.notes.add(new MidiNote(
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

    public NoteSequenceMatch findNoteSequence(List<Spelling> searchSpellings, long startFromTick) {
        List<MidiNote> allNotes = this.getNotes();
        allNotes.sort(Comparator.comparingLong(MidiNote::startTick));

        int startIndex = 0;
        while (startIndex < allNotes.size() && allNotes.get(startIndex).startTick() < startFromTick) {
            startIndex++;
        }

        for (int i = startIndex; i <= allNotes.size() - searchSpellings.size(); i++) {
            boolean matches = true;
            for (int j = 0; j < searchSpellings.size(); j++) {
                if (!allNotes.get(i + j).isEnharmonicWith(searchSpellings.get(j))) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                List<MidiNote> matchingNotes = allNotes.subList(i, i + searchSpellings.size());
                return new NoteSequenceMatch(matchingNotes, matchingNotes.getFirst().startTick());
            }
        }
        return null;
    }

    public List<MidiNote> findPhraseBetweenSequences(List<Spelling> startSpellings,
                                                     List<Spelling> endSpellings) {
        NoteSequenceMatch startMatch = findNoteSequence(startSpellings, 0);
        if (startMatch == null) {
            return List.of();
        }

        NoteSequenceMatch endMatch = findNoteSequence(endSpellings, startMatch.startTick() + 1);
        if (endMatch == null) {
            return List.of();
        }

        MidiNote lastNote = endMatch.sequence().get(endMatch.sequence().size() - 1);
        long phraseEndTick = lastNote.startTick() + lastNote.duration();
        return notes.stream()
                .filter(note -> note.startTick() >= startMatch.startTick() &&
                        note.startTick() < phraseEndTick)
                .sorted(Comparator.comparingLong(MidiNote::startTick))
                .toList();
    }

    public List<MidiNote> getNotes() {
        return notes;
    }

    public int getResolution() {
        return resolution;
    }
}
