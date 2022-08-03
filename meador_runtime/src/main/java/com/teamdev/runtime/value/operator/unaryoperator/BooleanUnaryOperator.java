package com.teamdev.runtime.value.operator.unaryoperator;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.value.type.bool.BooleanValue;
import com.teamdev.runtime.value.type.bool.BooleanValueVisitor;
import com.teamdev.runtime.value.type.Value;

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
    public Value apply(Value operand) throws MeadorRuntimeException {
        var visitor = new BooleanValueVisitor();
        operand.acceptVisitor(visitor);

        boolean booleanValue = visitor.value();
        return new BooleanValue(operator.apply(booleanValue));
    }
}
