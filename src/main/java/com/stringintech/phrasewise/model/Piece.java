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
    private int resolution;  // ticks per quarter note (PPQ)

    @JdbcTypeCode(SqlTypes.JSON_ARRAY)
    @Column(name = "notes", nullable = false)
    private List<Note> notes;

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

    public record NoteSequenceMatch(List<Note> sequence, long startTick) {
    }

    /**
     * Finds a sequence of notes with specified note names after a given start tick.
     *
     * @param searchNotes   the sequence of note names to search for
     * @param startFromTick the tick position to start searching from (inclusive)
     * @return A NoteSequenceMatch containing the matching notes and their start tick,
     * or null if no match is found
     */
    public NoteSequenceMatch findNoteSequence(List<NoteName> searchNotes, long startFromTick) {
        List<Note> allNotes = this.getNotes();
        allNotes.sort(Comparator.comparingLong(Note::getStartTick));

        // Find the first note that starts at or after startFromTick
        int startIndex = 0;
        while (startIndex < allNotes.size() && allNotes.get(startIndex).getStartTick() < startFromTick) {
            startIndex++;
        }

        // Look for the sequence starting from startIndex
        for (int i = startIndex; i <= allNotes.size() - searchNotes.size(); i++) {
            boolean matches = true;
            for (int j = 0; j < searchNotes.size(); j++) {
                NoteName expectedNote = searchNotes.get(j);
                NoteName actualNote = allNotes.get(i + j).getNoteName();

                if (!expectedNote.equals(actualNote)) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                List<Note> matchingNotes = allNotes.subList(i, i + searchNotes.size());
                return new NoteSequenceMatch(matchingNotes, matchingNotes.get(0).getStartTick());
            }
        }

        return null;
    }

    /**
     * Finds a sequence of notes with specified note names, starting from the beginning.
     *
     * @param searchNotes the sequence of note names to search for
     * @return A NoteSequenceMatch containing the matching notes and their start tick,
     * or null if no match is found
     */
    public NoteSequenceMatch findNoteSequence(List<NoteName> searchNotes) {
        return findNoteSequence(searchNotes, 0);
    }

    /**
     * Finds a musical phrase between two note sequences.
     *
     * @param startSequence the sequence of notes that starts the phrase
     * @param endSequence   the sequence of notes that ends the phrase
     * @return A list of all notes between and including the start and end sequences,
     * or an empty list if either sequence is not found
     */
    public List<Note> findPhraseBetweenSequences(List<NoteName> startSequence, List<NoteName> endSequence) {
        NoteSequenceMatch startMatch = findNoteSequence(startSequence);
        if (startMatch == null) {
            return List.of();
        }

        // Search for end sequence starting after the start sequence
        long searchFromTick = startMatch.startTick() + 1;
        NoteSequenceMatch endMatch = findNoteSequence(endSequence, searchFromTick);
        if (endMatch == null) {
            return List.of();
        }

        // Get all notes between start and end (inclusive)
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