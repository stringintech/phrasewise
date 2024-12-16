package com.stringintech.phrasewise.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

//@Entity FIXME
@Table(name = "pieces")
public class Piece {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String composer;

    @Column(name = "imported_at", nullable = false)
    private Instant importedAt;

    @Column(name = "resolution", nullable = false)
    private int resolution;

    @JdbcTypeCode(SqlTypes.JSON_ARRAY)
    @Column(name = "notes", nullable = false)
    private List<Note> notes;

    public record NoteSequenceMatch(List<Note> sequence, long startTick) {
    }

    public Piece() {
    }

    public Piece(Sequence sequence) {
        Track[] tracks = sequence.getTracks();
        if (tracks.length < 2) {
            throw new IllegalArgumentException("MIDI file must have at least 2 tracks");
        }
        Track mainTrack = tracks[1];
        List<Note> midiNotes = Note.listFromTrack(mainTrack);
        this.setResolution(sequence.getResolution());
        this.setNotes(midiNotes);
    }

    /**
     * Finds a sequence of notes with specified spellings in a given key context.
     *
     * @param searchSpellings List of target spellings
     * @param keyRoot         MIDI pitch number of the key's root note
     * @param startFromTick   Starting tick position (inclusive)
     * @return Matching sequence and start tick, or null if not found
     */
    public NoteSequenceMatch findNoteSequence(List<PitchSpelling.Spelling> searchSpellings, int keyRoot, long startFromTick) {
        List<Note> allNotes = this.getNotes();
        allNotes.sort(Comparator.comparingLong(Note::getStartTick));

        int startIndex = 0;
        while (startIndex < allNotes.size() && allNotes.get(startIndex).getStartTick() < startFromTick) {
            startIndex++;
        }

        for (int i = startIndex; i <= allNotes.size() - searchSpellings.size(); i++) {
            boolean matches = true;
            for (int j = 0; j < searchSpellings.size(); j++) {
                PitchSpelling.Spelling expectedSpelling = searchSpellings.get(j);
                PitchSpelling.Spelling actualSpelling = PitchSpelling.inKey(allNotes.get(i + j).getPitch(), keyRoot).getSpelling();

                if (!expectedSpelling.equals(actualSpelling)) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                List<Note> matchingNotes = allNotes.subList(i, i + searchSpellings.size());
                return new NoteSequenceMatch(matchingNotes, matchingNotes.get(0).getStartTick());
            }
        }
        return null;
    }

    /**
     * Finds a musical phrase between two note sequences in a given key context.
     *
     * @param startSpellings Starting sequence spellings
     * @param endSpellings   Ending sequence spellings
     * @param keyRoot        MIDI pitch number of the key's root note
     * @return Notes between and including the sequences, or empty list if not found
     */
    public List<Note> findPhraseBetweenSequences(List<PitchSpelling.Spelling> startSpellings,
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

        Note lastNote = endMatch.sequence().get(endMatch.sequence().size() - 1);
        long phraseEndTick = lastNote.getStartTick() + lastNote.getDuration();
        return notes.stream()
                .filter(note -> note.getStartTick() >= startMatch.startTick() &&
                        note.getStartTick() < phraseEndTick)
                .sorted(Comparator.comparingLong(Note::getStartTick))
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

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}