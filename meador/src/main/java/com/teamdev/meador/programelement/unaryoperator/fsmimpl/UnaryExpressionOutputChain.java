package com.teamdev.meador.programelement.unaryoperator.fsmimpl;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.evaluation.operator.AbstractUnaryOperator;

/**
 * Output chain for both {@link PrefixUnaryOperatorMachine} and {@link PostfixUnaryOperatorMachine}.
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
