package com.teamdev.runtime.evaluation.operandtype;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.evaluation.TypeMismatchException;

public class NumericValue implements Value {

    private final double value;

    public NumericValue(double value) {
        this.value = value;
    }

    @Override
    public void acceptVisitor(ValueVisitor visitor) throws TypeMismatchException {
        Preconditions.checkNotNull(visitor)
                     .visit(this);
    }

    public double numericValue() {
        return value;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(value);
        return (int) (temp ^ (temp >>> 32));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NumericValue that)) {
            return false;
        }

        return Double.compare(that.value, value) == 0;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
