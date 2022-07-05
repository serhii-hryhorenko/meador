package com.teamdev.fsm;

import java.util.function.Supplier;

public class ExceptionThrower<E extends Exception> {

    private final Supplier<E> exception;

    public ExceptionThrower(Supplier<E> exception) {
        this.exception = exception;
    }

    public void throwException() throws E {
        throw exception.get();
    }
}
