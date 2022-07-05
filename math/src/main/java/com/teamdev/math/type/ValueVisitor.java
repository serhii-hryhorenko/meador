package com.teamdev.math.type;

public interface ValueVisitor {

    void visit(DoubleValue value);

    void visit(BooleanValue value);
}
