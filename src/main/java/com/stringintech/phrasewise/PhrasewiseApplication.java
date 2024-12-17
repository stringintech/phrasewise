package com.stringintech.phrasewise;

import com.stringintech.phrasewise.midi.MidiNote;
import com.stringintech.phrasewise.legacy.model.MidiPiece;
import com.stringintech.phrasewise.legacy.model.PitchSpelling;
import com.stringintech.phrasewise.legacy.util.LilyPondHelper;
import com.stringintech.phrasewise.legacy.util.NoteSymbol;
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
            if (args.length < 3) {
                printUsage();
                System.exit(1);
            }

            String command = args[0];
            String midiPath = args[1];
            String keySymbol = args[2];
            int keyRoot = NoteSymbol.getKeyRoot(keySymbol);

            Sequence sequence = MidiSystem.getSequence(Path.of(midiPath).toFile());
            MidiPiece piece = new MidiPiece(sequence);

            try {
                switch (command) {
                    case "find-sequence" ->
                            handleFindSequence(piece, keyRoot, Arrays.copyOfRange(args, 3, args.length));
                    case "find-phrase" -> handleFindPhrase(piece, keyRoot, Arrays.copyOfRange(args, 3, args.length));
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

    private void handleFindSequence(MidiPiece piece, int keyRoot, String[] noteArgs) {
        if (noteArgs.length < 1) {
            System.err.println("Error: No notes provided for sequence search");
            printUsage();
            return;
        }

        List<PitchSpelling.Spelling> searchSpellings = NoteSymbol.spellingsFromSymbols(Arrays.asList(noteArgs));
        MidiPiece.NoteSequenceMatch match = piece.findNoteSequence(searchSpellings, keyRoot, 0);

        if (match == null) {
            System.out.println("No matching sequence found");
        } else {
            printFoundNotes("Found sequence", match.sequence());
            generateScore(match.sequence(), piece.getResolution());
        }
    }

    private void handleFindPhrase(MidiPiece piece, int keyRoot, String[] noteArgs) {
        if (noteArgs.length < 2) {
            System.err.println("Error: Both start and end sequences must be provided");
            printUsage();
            return;
        }

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

        List<PitchSpelling.Spelling> startSpellings = NoteSymbol.spellingsFromSymbols(startSeqArgs);
        List<PitchSpelling.Spelling> endSpellings = NoteSymbol.spellingsFromSymbols(endSeqArgs);

        List<MidiNote> phrase = piece.findPhraseBetweenSequences(startSpellings, endSpellings, keyRoot);

        if (phrase.isEmpty()) {
            System.out.println("No matching phrase found");
        } else {
            printFoundNotes("Found phrase", phrase);
            generateScore(phrase, piece.getResolution());
        }
    }

    private void printFoundNotes(String header, List<MidiNote> notes) {
        System.out.println(header + ":");
        for (MidiNote note : notes) {
            var spelling = PitchSpelling.inKey(note.pitch(), note.pitch()).getSpelling();
            System.out.printf("Note: %s%s at tick %d (duration: %d)%n", spelling.note(), spelling.accidental(), note.startTick(), note.duration());
        }
    }

    private void generateScore(List<MidiNote> notes, int resolution) {
        try {
            var dir = Path.of("/Users/kowsar/Downloads"); //TODO why middle man
            var lilyFile = dir.resolve("bach-phrase"); //TODO
            LilyPondHelper.createColoredScore(notes, resolution, 50, lilyFile); //TODO
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
        System.err.println("  find-sequence <midi-file-path> <key> <note1> <note2> ...");
        System.err.println("  find-phrase <midi-file-path> <key> <start-note1> <start-note2> ... -- <end-note1> <end-note2> ...");
        System.err.println();
        System.err.println("Examples:");
        System.err.println("  find-sequence path/to/midi.mid C C D E F");
        System.err.println("  find-phrase path/to/midi.mid Bb C D E -- G F E");
    }
}