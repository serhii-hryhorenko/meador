package com.teamdev.runtime.evaluation.operandtype;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.evaluation.TypeMismatchException;

public class StringValue implements Value {

    private final String string;

    public StringValue(String value) {
        this.string = Preconditions.checkNotNull(value);
    }

    @Override
    public void acceptVisitor(ValueVisitor visitor) throws TypeMismatchException {
        Preconditions.checkNotNull(visitor)
                     .visit(this);
    }

    public String string() {
        return string;
    }

    @Override
    public String toString() {
        return string;
    }
}
