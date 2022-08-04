package com.teamdev.meador.compiler;

import java.io.Serial;

/**
 * Exception thrown by {@link ProgramElementCompiler}. Indicates grammar mistake at Meador program.
 */
public class CompilingException extends Exception {

    public CompilingException(String message) {
        super(message);
    }

    public CompilingException() {
        super();
    }

    @Serial
    private static final long serialVersionUID = -2930223845734277001L;
}
