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
        List<Note> midiNotes = Note.createListFrom(mainTrack);
        this.setResolution(sequence.getResolution());
        this.setNotes(midiNotes);
    }

    public List<Note> findNoteSequence(List<NoteName> searchNotes) {
        List<Note> allNotes = this.getNotes();

        // Sort notes by start time to ensure correct sequence matching TODO aren't notes already sorted?
        allNotes.sort(Comparator.comparingLong(Note::getStartTick));

        // Look for the sequence in the sorted notes
        for (int i = 0; i <= allNotes.size() - searchNotes.size(); i++) {
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
                // Return the matching sequence
                return allNotes.subList(i, i + searchNotes.size());
            }
        }

        return List.of(); // Return empty list if no match found
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