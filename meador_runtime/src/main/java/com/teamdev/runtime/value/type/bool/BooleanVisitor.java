package com.teamdev.runtime.value.type.bool;

import com.teamdev.runtime.value.type.datastructure.DataStructureValue;
import com.teamdev.runtime.value.type.number.NumericValue;
import com.teamdev.runtime.value.type.string.StringValue;
import com.teamdev.runtime.value.type.ValueVisitor;

public class BooleanVisitor implements ValueVisitor {

    private boolean value;

    @Override
    public void visit(NumericValue value) {
        throw new IllegalArgumentException("Type mismatch. Expected: Boolean.");
    }

    @Override
    public void visit(BooleanValue value) {
        this.value = value.booleanValue();
    }

    @Override
    public void visit(DataStructureValue value) {
        throw new IllegalArgumentException("Type mismatch. Expected: Boolean.");
    }

    @Override
    public void visit(StringValue value) {
        throw new IllegalArgumentException("Type mismatch. Expected: Boolean.");
    }

    public boolean value() {
        return value;
    }
}
