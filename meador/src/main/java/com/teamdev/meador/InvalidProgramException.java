package com.teamdev.meador;

import com.teamdev.meador.programelement.SyntaxException;

import java.io.Serial;

/**
 * Exception thrown to user when either {@link SyntaxException} or
 * {@link com.teamdev.runtime.MeadorRuntimeException} is occurred.
 */
public class InvalidProgramException extends Exception {

    private int errorPosition;
    private boolean compilationError;

    public int errorPosition() {
        return errorPosition;
    }

    public void setErrorPosition(int errorPosition) {
        this.errorPosition = errorPosition;
        this.compilationError = true;
    }

    public boolean isCompilationError() {
        return compilationError;
    }

    public InvalidProgramException(int errorPosition) {
        this.errorPosition = errorPosition;
    }

    @Serial
    private static final long serialVersionUID = -6466180255612002503L;

    public InvalidProgramException(String message) {
        super(message);
    }
}
