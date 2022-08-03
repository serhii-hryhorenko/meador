package com.teamdev.runtime.value.type.datastructure;

import com.teamdev.runtime.value.type.Value;
import com.teamdev.runtime.value.type.ValueVisitor;

public class DataStructureValue implements Value {

    private final DataStructureHolder value;

    public DataStructureValue(DataStructureHolder value) {
        this.value = value;
    }

    @Override
    public void acceptVisitor(ValueVisitor visitor) {
        visitor.visit(this);
    }

    public DataStructureHolder dataStructureValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
