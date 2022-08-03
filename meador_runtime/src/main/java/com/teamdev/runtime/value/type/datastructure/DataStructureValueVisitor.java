package com.teamdev.runtime.value.type.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.value.type.ValueVisitor;
import com.teamdev.runtime.value.type.bool.BooleanValue;
import com.teamdev.runtime.value.type.number.NumericValue;
import com.teamdev.runtime.value.type.string.StringValue;

public class DataStructureValueVisitor implements ValueVisitor {
    private static final String ERROR_MESSAGE = "Type mismatch. Expected: Data structure.";

    private DataStructureHolder value;

    public DataStructureHolder value() {
        return Preconditions.checkNotNull(value);
    }

    @Override
    public void visit(NumericValue value) throws MeadorRuntimeException {
        throw new MeadorRuntimeException(ERROR_MESSAGE);
    }

    @Override
    public void visit(BooleanValue value) throws MeadorRuntimeException {
        throw new MeadorRuntimeException(ERROR_MESSAGE);
    }

    @Override
    public void visit(DataStructureValue value) {
        this.value = Preconditions.checkNotNull(value).dataStructureValue();
    }

    @Override
    public void visit(StringValue value) throws MeadorRuntimeException {
        throw new MeadorRuntimeException(ERROR_MESSAGE);
    }
}
