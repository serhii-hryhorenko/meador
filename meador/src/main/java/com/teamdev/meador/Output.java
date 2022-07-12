package com.teamdev.meador;

import com.google.common.base.Preconditions;

import java.io.OutputStream;

/**
 * A result of executing Meador {@link Program}.
 */
public class Output {
    private final OutputStream outputStream;

    public Output(OutputStream stream) {
        outputStream = Preconditions.checkNotNull(stream);
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public String toString() {
        return outputStream.toString();
    }
}
