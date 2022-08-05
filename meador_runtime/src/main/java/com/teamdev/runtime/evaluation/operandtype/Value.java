package com.teamdev.runtime.evaluation.operandtype;

import com.teamdev.runtime.evaluation.TypeMismatchException;

public interface Value {

    void acceptVisitor(ValueVisitor visitor) throws TypeMismatchException;

    @Override
    String toString();
}
