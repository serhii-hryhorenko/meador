package com.teamdev.runtime.value.operator.bioperator;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.value.type.bool.BooleanValue;
import com.teamdev.runtime.value.type.bool.BooleanValueVisitor;
import com.teamdev.runtime.value.type.Value;

import java.util.function.BiPredicate;

public class BooleanValueBinaryOperator extends AbstractBinaryOperator {

    private final BiPredicate<Boolean, Boolean> operator;

    public BooleanValueBinaryOperator(BiPredicate<Boolean, Boolean> operator, Priority priority) {
        super(priority);
        this.operator = Preconditions.checkNotNull(operator);
    }

    @Override
    public Value apply(Value left, Value right) throws MeadorRuntimeException {
        var visitor = new BooleanValueVisitor();

        left.acceptVisitor(visitor);
        boolean one = visitor.value();

        right.acceptVisitor(visitor);
        boolean two = visitor.value();

        return new BooleanValue(operator.test(one, two));
    }
}
