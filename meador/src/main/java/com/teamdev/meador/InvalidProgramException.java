package com.teamdev.meador;

import java.io.Serial;

public class InvalidProgramException extends Exception {

    public InvalidProgramException(String message) {
        super(message);
    }

    @Serial
    private static final long serialVersionUID = -6466180255612002503L;
}
