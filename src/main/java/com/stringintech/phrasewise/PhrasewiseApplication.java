package com.stringintech.phrasewise;

import com.stringintech.phrasewise.core.Key;
import com.stringintech.phrasewise.core.Spelling;
import com.stringintech.phrasewise.legacy.util.LilyPondHelper;
import com.stringintech.phrasewise.midi.MidiNote;
import com.stringintech.phrasewise.midi.MonophonicMidiSequence;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
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
            Spelling tonic = Spelling.fromSymbol(keySymbol);
            Key key = new Key(tonic, Key.Mode.MINOR);

            Sequence sequence = MidiSystem.getSequence(Path.of(midiPath).toFile());
            Track[] tracks = sequence.getTracks();
            if (tracks.length < 2) {
                throw new IllegalArgumentException("MIDI file must have at least 2 tracks");
            }
            Track mainTrack = tracks[1];
            MonophonicMidiSequence piece = new MonophonicMidiSequence(mainTrack, sequence.getResolution());

            try {
                switch (command) {
                    case "find-sequence" -> handleFindSequence(piece, key, Arrays.copyOfRange(args, 3, args.length));
                    case "find-phrase" -> handleFindPhrase(piece, key, Arrays.copyOfRange(args, 3, args.length));
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

    private void handleFindSequence(MonophonicMidiSequence piece, Key key, String[] noteArgs) {
        if (noteArgs.length < 1) {
            System.err.println("Error: No notes provided for sequence search");
            printUsage();
            return;
        }

        List<Spelling> searchSpellings = Spelling.listFromSymbols(Arrays.asList(noteArgs));
        MonophonicMidiSequence.NoteSequenceMatch match = piece.findNoteSequence(searchSpellings, 0);

        if (match == null) {
            System.out.println("No matching sequence found");
        } else {
            generateScore(match.sequence(), piece.getResolution(), key);
        }
    }

    private void handleFindPhrase(MonophonicMidiSequence piece, Key key, String[] noteArgs) {
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

        List<Spelling> startSpellings = Spelling.listFromSymbols(startSeqArgs);
        List<Spelling> endSpellings = Spelling.listFromSymbols(endSeqArgs);

        List<MidiNote> phrase = piece.findPhraseBetweenSequences(startSpellings, endSpellings);

        if (phrase.isEmpty()) {
            System.out.println("No matching phrase found");
        } else {
            generateScore(phrase, piece.getResolution(), key);
        }
    }

    private void generateScore(List<MidiNote> notes, int resolution, Key key) {
        try {
            var dir = Path.of("/Users/kowsar/Downloads"); //TODO why middle man
            var lilyFile = dir.resolve("bach-phrase"); //TODO
            LilyPondHelper.createColoredScore(notes, resolution, key, lilyFile); //TODO
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