package com.teamdev.runtime.value.type;

public interface Value {

    void acceptVisitor(ValueVisitor visitor);

    @Override
    String toString();
}
