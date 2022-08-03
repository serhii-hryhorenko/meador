package com.teamdev.runtime.value.type.number;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.type.datastructure.DataStructureValue;
import com.teamdev.runtime.value.type.string.StringValue;
import com.teamdev.runtime.value.type.ValueVisitor;
import com.teamdev.runtime.value.type.bool.BooleanValue;

public class NumericValueVisitor implements ValueVisitor {
    private static final String ERROR_MESSAGE = "Type mismatch. Expected: Numeric.";

    private double value;

    @Override
    public void visit(NumericValue value) {
        this.value = Preconditions.checkNotNull(value).numericValue();
    }

    @Override
    public void visit(BooleanValue value) {
        throw new IllegalArgumentException(ERROR_MESSAGE);
    }

    @Override
    public void visit(DataStructureValue value) {
        throw new IllegalArgumentException(ERROR_MESSAGE);
    }

    @Override
    public void visit(StringValue value) {
        throw new IllegalArgumentException(ERROR_MESSAGE);
    }

    public double value() {
        return value;
    }
}
