package com.teamdev.runtime.value.operator.unaryoperator;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.type.BooleanValue;
import com.teamdev.runtime.value.type.BooleanVisitor;
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
    public Value apply(Value value) {
        var visitor = new BooleanVisitor();
        value.acceptVisitor(visitor);

        boolean booleanValue = visitor.value();
        return new BooleanValue(operator.apply(booleanValue));
    }
}
