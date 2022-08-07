package com.teamdev.meador.programelement.expression.fsmimpl.string;

import com.google.common.base.Preconditions;

/**
 * Output chain for {@link StringLiteralMachine}.
 */
public class StringLiteralOutputChain {

    private String stringValue;

    public String string() {
        return Preconditions.checkNotNull(stringValue);
    }

    public void setStringValue(String stringValue) {
        this.stringValue = Preconditions.checkNotNull(stringValue);
    }
}
