package com.teamdev.math.type;

public class BooleanVisitor implements ValueVisitor {

    private boolean value;

    @Override
    public void visit(DoubleValue value) {
        throw new IllegalArgumentException("Type mismatch. Expected: Boolean.");
    }

    @Override
    public void visit(BooleanValue value) {
        this.value = value.value();
    }

    public boolean value() {
        return value;
    }
}
