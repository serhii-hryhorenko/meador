package com.teamdev.runtime.evaluation.operandtype;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.evaluation.TypeMismatchException;

public class BooleanValue implements Value {

    private final boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    @Override
    public void acceptVisitor(ValueVisitor visitor) throws TypeMismatchException {
        Preconditions.checkNotNull(visitor)
                     .visit(this);
    }

    public boolean booleanValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return (value ? 1 : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BooleanValue that)) {
            return false;
        }

        return value == that.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
