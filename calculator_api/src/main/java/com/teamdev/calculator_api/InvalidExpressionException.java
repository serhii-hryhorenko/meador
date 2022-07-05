package com.teamdev.calculator_api;

import java.io.Serial;

public class InvalidExpressionException extends Exception {

    @Serial
    private static final long serialVersionUID = 7654150467494556451L;
    private final int index;

    public InvalidExpressionException(String message, int index) {
        super(message + ", index of an invalid char: " + index);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
