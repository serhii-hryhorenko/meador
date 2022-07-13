package com.teamdev.meador.compiler.statement.relative_expr;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.bioperator.AbstractBinaryOperator;

import java.util.Objects;

/**
 * Output chain for {@link com.teamdev.meador.fsmimpl.util.RelationalExpressionFSM}.
 */
public class RelationalExpressionContext {
    private Command left;
    private Command right;
    private AbstractBinaryOperator operator;

    public Command left() {
        Preconditions.checkState(Objects.nonNull(left));
        return left;
    }

    public void setLeft(Command left) {
        this.left = Preconditions.checkNotNull(left);
    }

    public Command right() {
        Preconditions.checkState(Objects.nonNull(right));
        return right;
    }

    public void setRight(Command right) {
        this.right = Preconditions.checkNotNull(right);
    }

    public AbstractBinaryOperator operator() {
        return operator;
    }

    public void setOperator(AbstractBinaryOperator operator) {
        this.operator = Preconditions.checkNotNull(operator);
    }
}
