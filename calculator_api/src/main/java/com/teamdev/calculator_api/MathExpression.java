package com.teamdev.calculator_api;

import com.google.common.base.Preconditions;

/**
 * Tiny type Value-Object of user input for {@link Calculator}.
 */

public class MathExpression {

    private final String source;

    public MathExpression(String source) {
        this.source = Preconditions.checkNotNull(source);
    }

    public String getSource() {
        return source;
    }
}
