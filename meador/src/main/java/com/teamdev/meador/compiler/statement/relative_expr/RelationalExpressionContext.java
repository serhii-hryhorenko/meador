package com.teamdev.meador.compiler.statement.relative_expr;

import com.google.common.base.Preconditions;
import com.teamdev.math.bioperator.AbstractBinaryOperator;
import com.teamdev.meador.runtime.Command;

public class RelationalExpressionContext {
    private Command left;
    private Command right;
    private AbstractBinaryOperator operator;

    public Command left() {
        return left;
    }

    public RelationalExpressionContext setLeft(Command left) {
        this.left = Preconditions.checkNotNull(left);
        return this;
    }

    public Command right() {
        return right;
    }

    public RelationalExpressionContext setRight(Command right) {
        this.right = Preconditions.checkNotNull(right);
        return this;
    }

    public AbstractBinaryOperator operator() {
        return operator;
    }

    public RelationalExpressionContext setOperator(AbstractBinaryOperator operator) {
        this.operator = Preconditions.checkNotNull(operator);
        return this;
    }
}
