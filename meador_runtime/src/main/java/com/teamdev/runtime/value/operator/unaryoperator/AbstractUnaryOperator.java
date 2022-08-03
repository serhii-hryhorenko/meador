package com.teamdev.runtime.value.operator.unaryoperator;

import com.teamdev.runtime.value.type.Value;

/**
 * Abstract unary operator which takes {@link Value} and mutate it, has to return a mutated copy of the original.
 * Can be applied only to Meador variables.
 * Always mutates variable value in the postfix position.
 * Can mutate a variable value in prefix position if the parameter given to the constructor is true.
 */
public abstract class AbstractUnaryOperator implements UnaryOperator {

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
