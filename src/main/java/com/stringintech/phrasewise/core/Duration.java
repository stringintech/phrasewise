package com.stringintech.phrasewise.core;

public enum Duration {
    WHOLE,
    HALF,
    QUARTER,
    EIGHTH,
    SIXTEENTH,
    THIRTY_SECOND;

    DottedDuration getDotted() {
        return new DottedDuration(this);
    }
}