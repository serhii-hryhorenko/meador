package com.teamdev.runtime.evaluation.operandtype;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.evaluation.TypeMismatchException;

public class DataStructureValueVisitor implements ValueVisitor {

    private static final String ERROR_MESSAGE = "Type mismatch. Expected: Data structure.";

    private DataStructureHolder value;

    public DataStructureHolder value() {
        return Preconditions.checkNotNull(value);
    }

    @Override
    public void visit(NumericValue value) throws TypeMismatchException {
        throw new TypeMismatchException(ERROR_MESSAGE);
    }

    @Override
    public void visit(BooleanValue value) throws TypeMismatchException {
        throw new TypeMismatchException(ERROR_MESSAGE);
    }

    @Override
    public void visit(DataStructureValue value) {
        this.value = Preconditions.checkNotNull(value)
                                  .dataStructureValue();
    }

    @Override
    public void visit(StringValue value) throws TypeMismatchException {
        throw new TypeMismatchException(ERROR_MESSAGE);
    }
}
