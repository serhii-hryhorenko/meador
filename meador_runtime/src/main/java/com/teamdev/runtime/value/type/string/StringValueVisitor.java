package com.teamdev.runtime.value.type.string;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.type.ValueVisitor;
import com.teamdev.runtime.value.type.bool.BooleanValue;
import com.teamdev.runtime.value.type.datastructure.DataStructureValue;
import com.teamdev.runtime.value.type.number.NumericValue;

public class StringValueVisitor implements ValueVisitor {
    private static final String ERROR_MESSAGE = "Type mismatch. Expected: String.";

    private String value;

    @Override
    public void visit(NumericValue value) {
        throw new IllegalArgumentException(ERROR_MESSAGE);
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
        this.value = Preconditions.checkNotNull(value).string();
    }

    public String value() {
        return value;
    }
}
