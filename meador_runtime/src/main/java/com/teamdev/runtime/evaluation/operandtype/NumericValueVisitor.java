package com.teamdev.runtime.evaluation.operandtype;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.evaluation.TypeMismatchException;

public class NumericValueVisitor implements ValueVisitor {

    private static final String ERROR_MESSAGE = "Type mismatch. Expected: Numeric.";

    private double value;

    @Override
    public void visit(NumericValue value) {
        this.value = Preconditions.checkNotNull(value)
                                  .numericValue();
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
    public void visit(StringValue value) throws TypeMismatchException {
        throw new TypeMismatchException(ERROR_MESSAGE);
    }

    public double value() {
        return value;
    }
}
