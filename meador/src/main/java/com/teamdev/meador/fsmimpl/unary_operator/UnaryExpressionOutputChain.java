package com.teamdev.meador.fsmimpl.unary_operator;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.operator.unaryoperator.AbstractUnaryOperator;

/**
 * Output chain for {@link PrefixOperatorFSM} and {@link PostfixOperatorFSM}.
 */
public class UnaryExpressionOutputChain {

    private String variableName;
    private AbstractUnaryOperator unaryOperator;

    public String variableName() {
        return Preconditions.checkNotNull(variableName);
    }

    public void setVariableName(String variableName) {
        this.variableName = Preconditions.checkNotNull(variableName);
    }

    public AbstractUnaryOperator unaryOperator() {
        return unaryOperator;
    }

    public void setUnaryOperator(AbstractUnaryOperator unaryOperator) {
        this.unaryOperator = Preconditions.checkNotNull(unaryOperator);
    }
}
