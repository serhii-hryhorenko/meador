package com.teamdev.runtime.value.type;

import com.teamdev.runtime.datastructure.DataStructureHolder;

public class DataStructureValue implements Value {

    private final DataStructureHolder value;

    public DataStructureValue(DataStructureHolder dataStructure) {
        this.value = dataStructure;
    }

    @Override
    public void acceptVisitor(ValueVisitor visitor) {
        visitor.visit(this);
    }

    public DataStructureHolder value() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
