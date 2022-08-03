package com.teamdev.runtime.value.type.number;

import com.teamdev.runtime.value.type.Value;
import com.teamdev.runtime.value.type.ValueVisitor;

public class NumericValue implements Value {

    private final double value;

    public NumericValue(double value) {
        this.value = value;
    }

    @Override
    public void acceptVisitor(ValueVisitor visitor) {
        visitor.visit(this);
    }

    public double numericValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumericValue)) return false;

        NumericValue that = (NumericValue) o;

        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(value);
        return (int) (temp ^ (temp >>> 32));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
