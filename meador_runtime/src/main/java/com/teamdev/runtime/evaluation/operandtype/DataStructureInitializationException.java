package com.teamdev.runtime.evaluation.operandtype;

import java.io.Serial;

public class DataStructureInitializationException extends Exception {

    @Serial
    private static final long serialVersionUID = 6619337027770258700L;

    public DataStructureInitializationException(String message) {
        super(message);
    }
}
