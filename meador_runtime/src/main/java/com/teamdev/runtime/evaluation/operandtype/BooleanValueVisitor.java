package com.teamdev.runtime.evaluation.operandtype;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.evaluation.TypeMismatchException;

public class BooleanValueVisitor implements ValueVisitor {

    private static final String ERROR_MESSAGE = "Type mismatch. Expected: Boolean.";

    private boolean value;

    @Override
    public void visit(NumericValue value) throws TypeMismatchException {
        throw new TypeMismatchException(ERROR_MESSAGE);
    }

    @Override
    public void visit(BooleanValue value) {
        this.value = Preconditions.checkNotNull(value)
                                  .booleanValue();
    }

    @Override
    public void visit(DataStructureValue value) throws TypeMismatchException {
        throw new TypeMismatchException(ERROR_MESSAGE);
    }

    @Override
    public void visit(StringValue value) throws TypeMismatchException {
        throw new TypeMismatchException(ERROR_MESSAGE);
    }

    public boolean value() {
        return value;
    }
}
