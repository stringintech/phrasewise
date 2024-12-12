package com.stringintech.phrasewise.model;

import java.util.List;
import java.util.stream.Collectors;

public enum NoteName {
    C(1),
    C_SHARP(2),
    D(3),
    D_SHARP(4),
    E(5),
    F(6),
    F_SHARP(7),
    G(8),
    G_SHARP(9),
    A(10),
    A_SHARP(11),
    B(12);

    private final int value;

    NoteName(int value) {
        this.value = value;
    }

    public static List<NoteName> listFromSymbols(List<String> noteSymbols) {
        return noteSymbols.stream()
                .map(NoteName::fromSymbol)
                .collect(Collectors.toList());
    }

    public static NoteName fromSymbol(String noteSymbol) {
        return switch (noteSymbol) {
            case "C" -> NoteName.C;
            case "C#" -> NoteName.C_SHARP;
            case "D" -> NoteName.D;
            case "D#" -> NoteName.D_SHARP;
            case "E" -> NoteName.E;
            case "F" -> NoteName.F;
            case "F#" -> NoteName.F_SHARP;
            case "G" -> NoteName.G;
            case "G#" -> NoteName.G_SHARP;
            case "A" -> NoteName.A;
            case "A#" -> NoteName.A_SHARP;
            case "B" -> NoteName.B;
            default -> throw new IllegalArgumentException("Invalid note: " + noteSymbol);
        };
    }

    public int getValue() {
        return value;
    }
}