package com.teamdev.runtime.value.type.bool;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.value.type.datastructure.DataStructureValue;
import com.teamdev.runtime.value.type.number.NumericValue;
import com.teamdev.runtime.value.type.string.StringValue;
import com.teamdev.runtime.value.type.ValueVisitor;

public class BooleanValueVisitor implements ValueVisitor {
    private static final String ERROR_MESSAGE = "Type mismatch. Expected: Boolean.";

    private boolean value;

    @Override
    public void visit(NumericValue value) throws MeadorRuntimeException {
        throw new MeadorRuntimeException(ERROR_MESSAGE);
    }

    @Override
    public void visit(BooleanValue value) {
        this.value = Preconditions.checkNotNull(value).booleanValue();
    }

    @Override
    public void visit(DataStructureValue value) throws MeadorRuntimeException {
        throw new MeadorRuntimeException(ERROR_MESSAGE);
    }

    @Override
    public void visit(StringValue value) throws MeadorRuntimeException {
        throw new MeadorRuntimeException(ERROR_MESSAGE);
    }

    public boolean value() {
        return value;
    }
}
