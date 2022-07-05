package com.teamdev.math.type;

public class DoubleValue implements Value {

    private final double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    @Override
    public void acceptVisitor(ValueVisitor visitor) {
        visitor.visit(this);
    }

    public double value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
