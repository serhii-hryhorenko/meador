package com.teamdev.math.type;

public interface Value {

    void acceptVisitor(ValueVisitor visitor);
}
