package com.teamdev.runtime.value.operator.unaryoperator;

import com.teamdev.runtime.value.type.Value;

import java.util.function.UnaryOperator;

public abstract class AbstractUnaryOperator implements UnaryOperator<Value> {

    private final boolean prefixFormMutatesVariable;

    AbstractUnaryOperator() {
        this.prefixFormMutatesVariable = false;
    }

    AbstractUnaryOperator(boolean mutates) {
        this.prefixFormMutatesVariable = mutates;
    }

    public boolean prefixFormMutatesVariable() {
        return prefixFormMutatesVariable;
    }
}
