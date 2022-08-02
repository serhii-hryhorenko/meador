package com.teamdev.runtime.value.type;

public class DoubleValueVisitor implements ValueVisitor {

    private double value;

    @Override
    public void visit(DoubleValue value) {
        this.value = value.value();
    }

    @Override
    public void visit(BooleanValue value) {
        throw new IllegalArgumentException("Type mismatch. Expected: Double.");
    }

    @Override
    public void visit(StringValue value) {
        throw new IllegalArgumentException("Type mismatch. Expected: Double.");
    }

    public double value() {
        return value;
    }
}
