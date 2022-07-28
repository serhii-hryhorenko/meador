package com.teamdev.meador.fsmimpl.string_expression;

import com.google.common.base.Preconditions;

public class StringLiteralOutputChain {

    private String stringValue;

    public String string() {
        return Preconditions.checkNotNull(stringValue);
    }

    public void setStringValue(String stringValue) {
        this.stringValue = Preconditions.checkNotNull(stringValue);
    }
}
