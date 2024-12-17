package com.stringintech.phrasewise.midi;

public record MidiNote(int pitch, long startTick, long duration, int velocity, int channel) {
}