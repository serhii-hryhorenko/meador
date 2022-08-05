package com.teamdev.runtime.operatorimpl.unaryoperator;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.evaluation.TypeMismatchException;
import com.teamdev.runtime.evaluation.operandtype.NumericValue;
import com.teamdev.runtime.evaluation.operandtype.NumericValueVisitor;
import com.teamdev.runtime.evaluation.operandtype.Value;
import com.teamdev.runtime.evaluation.operator.AbstractUnaryOperator;

import java.util.function.DoubleUnaryOperator;

public class NumericUnaryOperator extends AbstractUnaryOperator {

    private final DoubleUnaryOperator operator;

    public NumericUnaryOperator(DoubleUnaryOperator operator) {
        super();
        this.operator = Preconditions.checkNotNull(operator);
    }

    public NumericUnaryOperator(DoubleUnaryOperator operator, boolean prefixFormMutatesVariable) {
        super(prefixFormMutatesVariable);
        this.operator = Preconditions.checkNotNull(operator);
    }

    @Override
    public Value apply(Value operand) throws TypeMismatchException {
        var visitor = new NumericValueVisitor();
        operand.acceptVisitor(visitor);

        double doubleValue = visitor.value();
        return new NumericValue(operator.applyAsDouble(doubleValue));
    }
}
