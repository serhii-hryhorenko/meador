package com.teamdev.meador;

import com.google.common.base.Preconditions;

/**
 * A Meador program which consists of a sequence of statements and operators.
 */
public class Program {
    private final String code;

    public Program(String code) {
        this.code = Preconditions.checkNotNull(code);
    }

    public String getCode() {
        return code;
    }
}
