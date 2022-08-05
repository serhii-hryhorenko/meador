package com.teamdev.runtime.evaluation.operator;

import com.teamdev.runtime.evaluation.TypeMismatchException;
import com.teamdev.runtime.evaluation.operandtype.Value;

/**
 * Abstract unary operator which takes {@link Value} and mutate it, has to return a mutated copy of
 * the original.
 * Can be applied only to Meador variables.
 * Always mutates variable value in the postfix position.
 * Can mutate a variable value in prefix position if the parameter given to the constructor is
 * true.
 */
public abstract class AbstractUnaryOperator {

    private final boolean prefixMutation;

    public abstract Value apply(Value operand) throws TypeMismatchException;

    protected AbstractUnaryOperator() {
        this.prefixMutation = false;
    }

    protected AbstractUnaryOperator(boolean prefixMutation) {
        this.prefixMutation = prefixMutation;
    }

    public boolean prefixFormMutatesVariable() {
        return prefixMutation;
    }
}
