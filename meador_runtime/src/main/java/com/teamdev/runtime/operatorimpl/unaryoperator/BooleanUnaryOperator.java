package com.teamdev.runtime.operatorimpl.unaryoperator;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.evaluation.TypeMismatchException;
import com.teamdev.runtime.evaluation.operandtype.BooleanValue;
import com.teamdev.runtime.evaluation.operandtype.BooleanValueVisitor;
import com.teamdev.runtime.evaluation.operandtype.Value;
import com.teamdev.runtime.evaluation.operator.AbstractUnaryOperator;

import java.util.function.UnaryOperator;

public class BooleanUnaryOperator extends AbstractUnaryOperator {

    private final UnaryOperator<Boolean> operator;

    public BooleanUnaryOperator(UnaryOperator<Boolean> operator) {
        super();
        this.operator = Preconditions.checkNotNull(operator);
    }

    public BooleanUnaryOperator(UnaryOperator<Boolean> operator, boolean prefixMutatesVariable) {
        super(prefixMutatesVariable);
        this.operator = Preconditions.checkNotNull(operator);
    }

    @Override
    public Value apply(Value operand) throws TypeMismatchException {
        var visitor = new BooleanValueVisitor();
        operand.acceptVisitor(visitor);

        boolean booleanValue = visitor.value();
        return new BooleanValue(operator.apply(booleanValue));
    }
}
