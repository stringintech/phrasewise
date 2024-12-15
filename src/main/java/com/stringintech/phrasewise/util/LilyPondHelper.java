package com.stringintech.phrasewise.util;

import com.stringintech.phrasewise.model.Note;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class LilyPondHelper {
    // Chromatic scale degree colors
    // https://www.musanim.com/HarmonicColoring/
    private static final Map<Integer, String> DEGREE_COLORS = Map.ofEntries(
            Map.entry(1, "#0000FF"), // I
            Map.entry(2, "#CCCC00"), // I#
            Map.entry(3, "#660099"), // II
            Map.entry(4, "#00CC99"), // IIIb
            Map.entry(5, "#FF0000"), // III
            Map.entry(6, "#0066FF"), // IV
            Map.entry(7, "#FFFF00"), // IV#
            Map.entry(8, "#660099"), // V
            Map.entry(9, "#00AA00"), // V#
            Map.entry(10, "#CC0099"), // VI
            Map.entry(11, "#00FFFF"), // VIIb
            Map.entry(12, "#FF6600") // VII
    );

    /**
     * Creates a colored score from a musical phrase, where each note is colored
     * according to its scale degree relative to the tonic.
     *
     * @param phrase     List of notes in the musical phrase
     * @param resolution MIDI resolution (ticks per quarter note)
     * @param tonicPitch The MIDI pitch number of the tonic note
     * @param outputPath Path where the LilyPond file should be saved
     * @throws IOException if there's an error writing the file
     */
    public static void createColoredScore(List<Note> phrase, int resolution, int tonicPitch, Path outputPath) throws IOException {
        StringBuilder lily = new StringBuilder();

        // Add version and required includes
        lily.append("\\version \"2.20.0\"\n\n");

        // Add score structure
        lily.append("\\score {\n");
        lily.append("  \\new Staff {\n");
        lily.append("    \\time 4/4\n");
        lily.append("    \\key ").append("d").append(" \\minor\n"); //TODO
        lily.append("    \\clef bass\n\n"); //TODO

        // Process each note in the phrase
        for (Note note : phrase) {
            String lilyNote = LilypondNotationHelper.midiPitchToLilyPond(note.getPitch(), tonicPitch);
            int chromaticDegree = calculateChromaticDegree(note.getPitch(), tonicPitch);
            String color = DEGREE_COLORS.get(chromaticDegree);

            // Add color override for this note
            String rgbValues = hexToRGBValues(color);
            lily.append(String.format("    \\once \\override NoteHead.color = #(rgb-color %s)\n", rgbValues));

            // Add the note with its duration
            lily.append("    ").append(lilyNote)
                    .append(calculateLilyPondDuration(note.getDuration(), resolution))
                    .append(" ");
        }

        // Close score structure
        lily.append("\n  }\n");
        lily.append("  \\layout { }\n");
        lily.append("  \\midi { }\n");
        lily.append("}\n");

        // Write to file
        try (FileWriter writer = new FileWriter(outputPath.toFile())) {
            writer.write(lily.toString());
        }
    }

    /**
     * Calculates the chromatic scale degree of a note relative to the tonic.
     */
    private static int calculateChromaticDegree(int notePitch, int tonicPitch) {
        return ((notePitch - tonicPitch + 12) % 12) + 1; //TODO ok?
    }

    /**
     * Calculates LilyPond duration notation from MIDI ticks.
     */
    private static String calculateLilyPondDuration(long ticks, int resolution) { //TODO support ties, dotted notes, ... they have sth to do with bars and time signature and ...
        if (resolution != 480) {
            throw new IllegalArgumentException("resolution must be 480");
        }
        // Assuming standard MIDI resolution of 480 ticks per quarter note TODO not true
        if (ticks >= 1920) return "1";      // whole note
        if (ticks >= 960) return "2";       // half note
        if (ticks >= 480) return "4";       // quarter note
        if (ticks >= 240) return "8";       // eighth note
        if (ticks >= 120) return "16";      // sixteenth note
        return "32";
    }

    /**
     * Converts a hex color string to RGB values suitable for LilyPond.
     */
    private static String hexToRGBValues(String hex) {
        String hexColor = hex.replace("#", "");
        float r = Integer.parseInt(hexColor.substring(0, 2), 16) / 255.0f;
        float g = Integer.parseInt(hexColor.substring(2, 4), 16) / 255.0f;
        float b = Integer.parseInt(hexColor.substring(4, 6), 16) / 255.0f;
        return String.format("%.2f %.2f %.2f", r, g, b);
    }

    /**
     * Compiles a LilyPond file to PDF using the lilypond command-line tool.
     */
    public static void compileToPDF(Path lilypondFile, Path outputDir) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "lilypond",
                "--output=" + outputDir.toString(),
                lilypondFile.toString()
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("LilyPond compilation failed with exit code: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("LilyPond compilation interrupted", e);
        }
    }
}