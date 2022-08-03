package com.teamdev.runtime.value.operator.unaryoperator;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.value.type.number.NumericValue;
import com.teamdev.runtime.value.type.number.NumericValueVisitor;
import com.teamdev.runtime.value.type.Value;

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
    public Value apply(Value operand) throws MeadorRuntimeException {
        var visitor = new NumericValueVisitor();
        operand.acceptVisitor(visitor);

        double doubleValue = visitor.value();
        return new NumericValue(operator.applyAsDouble(doubleValue));
    }
}
