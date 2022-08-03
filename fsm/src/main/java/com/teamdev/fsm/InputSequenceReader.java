package com.teamdev.fsm;

import com.google.common.base.Preconditions;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Reader for user String input operated by {@code StateAcceptor}.
 */
public class InputSequenceReader {

    private final char[] source;
    private Deque<Integer> savedPositions = new ArrayDeque<>();
    private int readingPosition;

    public InputSequenceReader(String value) {
        this.source = Preconditions.checkNotNull(value).toCharArray();
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

    public void savePosition() {
        savedPositions.push(readingPosition);
    }

    Deque<Integer> dumpState() {
        return new ArrayDeque<>(savedPositions);
    }

    void setState(Deque<Integer> state) {
        savedPositions = Preconditions.checkNotNull(state);
    }

    public void restorePosition() {
        Preconditions.checkState(!savedPositions.isEmpty(), "Can't pop empty save.");
        readingPosition = savedPositions.pop();
    }

    void skipWhitespaces() {
        while (canRead() && isSpaceChar()) {
            readingPosition++;
        }
    }

    private boolean isSpaceChar() {
        return Character.isWhitespace(read());
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
