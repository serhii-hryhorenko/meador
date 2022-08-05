package com.teamdev.runtime;

import java.io.Serial;

public class MeadorRuntimeException extends Exception {

    @Serial
    private static final long serialVersionUID = 8471119067628994528L;

    public MeadorRuntimeException(String message) {
        super(message);
    }
}
