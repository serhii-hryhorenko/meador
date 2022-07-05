package com.teamdev.meador;

import java.io.OutputStream;

public class Output {

    private final OutputStream outputStream;

    public Output(OutputStream stream) {
        outputStream = stream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public String toString() {
        return outputStream.toString();
    }
}
