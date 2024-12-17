package com.stringintech.phrasewise.core;

public class DottedDuration {
    private final Duration baseDuration;

    DottedDuration(Duration baseDuration) {
        this.baseDuration = baseDuration;
    }

    public Duration getBaseDuration() {
        return baseDuration;
    }
}