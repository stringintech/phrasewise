package com.stringintech.phrasewise.legacy.model;

import com.stringintech.phrasewise.midi.MidiNote;
import com.stringintech.phrasewise.midi.MonophonicSequence;

import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public class MidiPiece {
    private Long id;

    private String title;

    private String composer;

    private Instant importedAt;

    private int resolution;

    private List<MidiNote> notes;

    public record NoteSequenceMatch(List<MidiNote> sequence, long startTick) {
    }

    public MidiPiece() {
    }

    public MidiPiece(Sequence sequence) {
        Track[] tracks = sequence.getTracks();
        if (tracks.length < 2) {
            throw new IllegalArgumentException("MIDI file must have at least 2 tracks");
        }
        Track mainTrack = tracks[1];
        List<MidiNote> midiNotes = new MonophonicSequence(mainTrack).getNotes();
        this.setResolution(sequence.getResolution());
        this.setNotes(midiNotes);
    }

    public NoteSequenceMatch findNoteSequence(List<PitchSpelling.Spelling> searchSpellings, int keyRoot, long startFromTick) {
        List<MidiNote> allNotes = this.getNotes();
        allNotes.sort(Comparator.comparingLong(MidiNote::startTick));

        int startIndex = 0;
        while (startIndex < allNotes.size() && allNotes.get(startIndex).startTick() < startFromTick) {
            startIndex++;
        }

        for (int i = startIndex; i <= allNotes.size() - searchSpellings.size(); i++) {
            boolean matches = true;
            for (int j = 0; j < searchSpellings.size(); j++) {
                PitchSpelling.Spelling expectedSpelling = searchSpellings.get(j);
                PitchSpelling.Spelling actualSpelling = PitchSpelling.inKey(allNotes.get(i + j).pitch(), keyRoot).getSpelling();

                if (!expectedSpelling.equals(actualSpelling)) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                List<MidiNote> matchingNotes = allNotes.subList(i, i + searchSpellings.size());
                return new NoteSequenceMatch(matchingNotes, matchingNotes.get(0).startTick());
            }
        }
        return null;
    }

    public List<MidiNote> findPhraseBetweenSequences(List<PitchSpelling.Spelling> startSpellings,
                                                     List<PitchSpelling.Spelling> endSpellings,
                                                     int keyRoot) {
        NoteSequenceMatch startMatch = findNoteSequence(startSpellings, keyRoot, 0);
        if (startMatch == null) {
            return List.of();
        }

        NoteSequenceMatch endMatch = findNoteSequence(endSpellings, keyRoot, startMatch.startTick() + 1);
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public Instant getImportedAt() {
        return importedAt;
    }

    public void setImportedAt(Instant importedAt) {
        this.importedAt = importedAt;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public List<MidiNote> getNotes() {
        return notes;
    }

    public void setNotes(List<MidiNote> notes) {
        this.notes = notes;
    }
}