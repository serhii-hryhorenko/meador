package com.teamdev.runtime.evaluation.operandtype;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.evaluation.TypeMismatchException;

public class DataStructureValue implements Value {

    private final DataStructureHolder value;

    public DataStructureValue(DataStructureHolder value) {
        this.value = value;
    }

    @Override
    public void acceptVisitor(ValueVisitor visitor) throws TypeMismatchException {
        Preconditions.checkNotNull(visitor)
                     .visit(this);
    }

    public DataStructureHolder dataStructureValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
