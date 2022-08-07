package com.teamdev.meador;

import java.io.Serial;

/**
 * Exception thrown to user when either {@link com.teamdev.meador.programelement.CompilingException} or
 * {@link com.teamdev.runtime.MeadorRuntimeException} is occurred.
 */
public class InvalidProgramException extends Exception {

    @Serial
    private static final long serialVersionUID = -6466180255612002503L;

    public InvalidProgramException(String message) {
        super(message);
    }
}
