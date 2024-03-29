package com.teamdev.meador.programelement;

import java.io.Serial;

/**
 * Exception thrown by {@link ProgramElementCompiler}. Indicates grammar mistake at Meador program.
 */
public class SyntaxException extends Exception {

    @Serial
    private static final long serialVersionUID = -2930223845734277001L;

    public SyntaxException(String message) {
        super(message);
    }
}
