package com.teamdev.runtime.evaluation;

import java.io.Serial;

public class TypeMismatchException extends Exception{

    @Serial
    private static final long serialVersionUID = -348810566703841986L;

    public TypeMismatchException(String message) {
        super(message);
    }
}
