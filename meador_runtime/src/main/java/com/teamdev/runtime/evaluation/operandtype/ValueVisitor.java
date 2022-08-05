package com.teamdev.runtime.evaluation.operandtype;

import com.teamdev.runtime.evaluation.TypeMismatchException;

public interface ValueVisitor {

    void visit(NumericValue value) throws TypeMismatchException;

    void visit(BooleanValue value) throws TypeMismatchException;

    void visit(DataStructureValue value) throws TypeMismatchException;

    void visit(StringValue value) throws TypeMismatchException;
}
