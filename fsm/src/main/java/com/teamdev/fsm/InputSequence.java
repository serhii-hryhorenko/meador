package com.teamdev.fsm;

import com.google.common.base.Preconditions;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Readable string from Turing machine operated by {@code StateAcceptor}.
 */

public class InputSequence {

    private final char[] source;
    private final Deque<Integer> savedPositions = new ArrayDeque<>();
    private int readingPosition;

    public InputSequence(String value) {
        this.source = value.toCharArray();
    }

    public boolean canRead() {
        return readingPosition < source.length;
    }

    public char read() {
        return source[readingPosition];
    }

    public void next() {
        readingPosition++;
    }

    void savePosition() {
        savedPositions.push(readingPosition);
    }

    void restorePosition() {
        Preconditions.checkState(!savedPositions.isEmpty(), "Can't pop empty save.");
        readingPosition = savedPositions.pop();
    }

    void skipWhitespaces() {
        while (canRead() && isSpaceChar()) {
            readingPosition++;
        }
    }

    private boolean isSpaceChar() {
        return Character.isSpaceChar(read()) || String.valueOf(read()).equals(System.lineSeparator());
    }

    public int getPosition() {
        return readingPosition;
    }

    public String getSequence() {
        return String.valueOf(source)
                     .substring(readingPosition);
    }

    @Override
    public String toString() {
        return String.valueOf(source);
    }
}
