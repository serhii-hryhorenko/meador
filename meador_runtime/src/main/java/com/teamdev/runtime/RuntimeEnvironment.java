package com.teamdev.runtime;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Environment where a program is executed.
 * Provides access to {@link Memory}, {@link SystemStack}, and IO.
 */
public class RuntimeEnvironment {
    public static final int MAX_LOOP_ITERATIONS = 10000;

    private final Memory memory = new Memory();

    private final SystemStack stack = new SystemStack();

    private final ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();

    private final PrintStream outputStream = new PrintStream(byteArrayOut);

    public Memory memory() {
        return memory;
    }

    public SystemStack stack() {
        return stack;
    }

    public PrintStream output() {
        return outputStream;
    }

    public OutputStream console() {
        return byteArrayOut;
    }
}
