package com.teamdev.runtime.value.type;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.datastructure.DataStructureHolder;

public class DataStructureVisitor implements ValueVisitor {

    private DataStructureHolder value;

    @Override
    public void visit(DataStructureValue value) {
        this.value = Preconditions.checkNotNull(value).value();
    }

    @Override
    public void visit(DoubleValue value) {
        throw new IllegalArgumentException("Type mismatch. Expected: DataStructure.");
    }

    @Override
    public void visit(BooleanValue value) {
        throw new IllegalArgumentException("Type mismatch. Expected: DataStructure.");
    }

    public DataStructureHolder value() {
        Preconditions.checkState(value != null);
        return value;
    }
}
