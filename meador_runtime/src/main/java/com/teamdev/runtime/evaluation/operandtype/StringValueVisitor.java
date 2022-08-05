package com.teamdev.runtime.evaluation.operandtype;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.evaluation.TypeMismatchException;

public class StringValueVisitor implements ValueVisitor {

    private static final String ERROR_MESSAGE = "Type mismatch. Expected: String.";

    private String value;

    @Override
    public void visit(NumericValue value) throws TypeMismatchException {
        throw new TypeMismatchException(ERROR_MESSAGE);
    }

    @Override
    public void visit(BooleanValue value) throws TypeMismatchException {
        throw new TypeMismatchException(ERROR_MESSAGE);
    }

    @Override
    public void visit(DataStructureValue value) throws TypeMismatchException {
        throw new TypeMismatchException(ERROR_MESSAGE);
    }

    @Override
    public void visit(StringValue value) {
        this.value = Preconditions.checkNotNull(value)
                                  .string();
    }

    public String value() {
        return value;
    }
}
