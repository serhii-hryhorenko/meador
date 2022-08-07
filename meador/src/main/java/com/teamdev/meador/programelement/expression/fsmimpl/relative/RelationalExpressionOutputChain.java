package com.teamdev.meador.programelement.expression.fsmimpl.relative;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.evaluation.operator.AbstractBinaryOperator;

/**
 * Output chain for {@link RelationalExpressionMachine}.
 */
public class RelationalExpressionOutputChain {

    private Command left;
    private Command right;
    private AbstractBinaryOperator operator;

    public Command left() {
        return Preconditions.checkNotNull(left);
    }

    public void setLeft(Command left) {
        this.left = Preconditions.checkNotNull(left);
    }

    public Command right() {
        return Preconditions.checkNotNull(right);
    }

    public void setRight(Command right) {
        this.right = Preconditions.checkNotNull(right);
    }

    public AbstractBinaryOperator operator() {
        return Preconditions.checkNotNull(operator);
    }

    public void setOperator(AbstractBinaryOperator operator) {
        this.operator = Preconditions.checkNotNull(operator);
    }
}
