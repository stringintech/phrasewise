package com.stringintech.phrasewise;

import com.stringintech.phrasewise.model.Note;
import com.stringintech.phrasewise.model.NoteName;
import com.stringintech.phrasewise.model.Piece;
import com.stringintech.phrasewise.util.LilyPondHelper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class PhrasewiseApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhrasewiseApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            if (args.length < 2) {
                printUsage();
                System.exit(1);
            }

            String command = args[0];
            String midiPath = args[1];
            Sequence sequence = MidiSystem.getSequence(Path.of(midiPath).toFile());
            Piece piece = new Piece(sequence);

            try {
                switch (command) {
                    case "find-sequence" -> handleFindSequence(piece, Arrays.copyOfRange(args, 2, args.length));
                    case "find-phrase" -> handleFindPhrase(piece, Arrays.copyOfRange(args, 2, args.length));
                    default -> {
                        System.err.println("Unknown command: " + command);
                        printUsage();
                        System.exit(1);
                    }
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
                System.exit(1);
            }
        };
    }

    private void handleFindSequence(Piece piece, String[] noteArgs) {
        if (noteArgs.length < 1) {
            System.err.println("Error: No notes provided for sequence search");
            printUsage();
            return;
        }

        List<NoteName> searchNotes = NoteName.listFromSymbols(Arrays.asList(noteArgs)); //TODO catch exception
//            System.err.println("Invalid note names. Valid notes are: " +
//                    String.join(", ", NOTE_NAMES));
//            System.exit(1);

        Piece.NoteSequenceMatch match = piece.findNoteSequence(searchNotes);

        if (match == null) {
            System.out.println("No matching sequence found");
        } else {
            printFoundNotes("Found sequence", match.sequence());
            generateScore(match.sequence(), piece.getResolution());
        }
    }

    private void handleFindPhrase(Piece piece, String[] noteArgs) {
        if (noteArgs.length < 2) {
            System.err.println("Error: Both start and end sequences must be provided");
            printUsage();
            return;
        }

        // Split args into start and end sequences
        int separatorIndex = indexOf(noteArgs, "--");
        if (separatorIndex == -1) {
            System.err.println("Error: Missing separator '--' between start and end sequences");
            printUsage();
            return;
        }

        List<String> startSeqArgs = Arrays.asList(Arrays.copyOfRange(noteArgs, 0, separatorIndex));
        List<String> endSeqArgs = Arrays.asList(Arrays.copyOfRange(noteArgs, separatorIndex + 1, noteArgs.length));

        if (startSeqArgs.isEmpty() || endSeqArgs.isEmpty()) {
            System.err.println("Error: Both start and end sequences must be provided");
            printUsage();
            return;
        }

        List<NoteName> startSequence = NoteName.listFromSymbols(startSeqArgs); //TODO catch exception
        List<NoteName> endSequence = NoteName.listFromSymbols(endSeqArgs); //TODO catch exception

        List<Note> phrase = piece.findPhraseBetweenSequences(startSequence, endSequence);

        if (phrase.isEmpty()) {
            System.out.println("No matching phrase found");
        } else {
            printFoundNotes("Found phrase", phrase);
            generateScore(phrase, piece.getResolution());
        }
    }

    private void printFoundNotes(String header, List<Note> notes) {
        System.out.println(header + ":");
        for (Note note : notes) {
            System.out.printf("Note: %s%d at tick %d (duration: %d)%n",
                    note.getNoteName(),
                    note.getOctave(),
                    note.getStartTick(),
                    note.getDuration());
        }
    }

    private void generateScore(List<Note> notes, int resolution) {
        try {
            var dir = Path.of("/Users/kowsar/Downloads");
            var lilyFile = dir.resolve("bach-phrase"); //TODO why middle man
            LilyPondHelper.createColoredScore(notes, resolution, NoteName.D, lilyFile);
            LilyPondHelper.compileToPDF(lilyFile, dir);
        } catch (Exception e) {
            System.err.println("Error generating score: " + e.getMessage());
        }
    }

    private int indexOf(String[] array, String target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }

    private void printUsage() {
        System.err.println("Usage:");
        System.err.println("  find-sequence <midi-file-path> <note1> <note2> ...");
        System.err.println("  find-phrase <midi-file-path> <start-note1> <start-note2> ... -- <end-note1> <end-note2> ...");
        System.err.println();
        System.err.println("Examples:");
        System.err.println("  find-sequence path/to/midi.mid C D E F");
        System.err.println("  find-phrase path/to/midi.mid C D E -- G F E");
    }
}