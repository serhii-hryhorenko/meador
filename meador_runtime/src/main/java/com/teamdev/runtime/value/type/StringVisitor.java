package com.teamdev.runtime.value.type;

public class StringVisitor implements ValueVisitor {

    private String value;

    @Override
    public void visit(DoubleValue value) {
        throw new IllegalArgumentException("Type mismatch. Expected: String.");
    }

    @Override
    public void visit(BooleanValue value) {
        throw new IllegalArgumentException("Type mismatch. Expected: String.");
    }

    @Override
    public void visit(StringValue value) {
        this.value = value.string();
    }

    public String value() {
        return value;
    }
}
