package com.teamdev.math.type;

public class BooleanValue implements Value {

    private final boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    @Override
    public void acceptVisitor(ValueVisitor visitor) {
        visitor.visit(this);
    }

    public boolean value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
