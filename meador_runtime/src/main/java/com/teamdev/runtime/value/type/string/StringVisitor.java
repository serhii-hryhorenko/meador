package com.teamdev.runtime.value.type.string;

import com.teamdev.runtime.value.type.ValueVisitor;
import com.teamdev.runtime.value.type.bool.BooleanValue;
import com.teamdev.runtime.value.type.datastructure.DataStructureValue;
import com.teamdev.runtime.value.type.number.NumericValue;

public class StringVisitor implements ValueVisitor {

    private String value;

    @Override
    public void visit(NumericValue value) {
        throw new IllegalArgumentException("Type mismatch. Expected: String.");
    }

    @Override
    public void visit(BooleanValue value) {
        throw new IllegalArgumentException("Type mismatch. Expected: String.");
    }

    @Override
    public void visit(DataStructureValue value) {
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
