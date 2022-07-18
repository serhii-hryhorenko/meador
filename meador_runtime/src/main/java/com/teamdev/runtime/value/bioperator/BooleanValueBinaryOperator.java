package com.teamdev.runtime.value.bioperator;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.type.BooleanValue;
import com.teamdev.runtime.value.type.BooleanVisitor;
import com.teamdev.runtime.value.type.Value;

import java.util.function.BiPredicate;

public class BooleanValueBinaryOperator extends AbstractBinaryOperator {

    private final BiPredicate<Boolean, Boolean> operator;

    public BooleanValueBinaryOperator(BiPredicate<Boolean, Boolean> operator, Priority priority) {
        super(priority);
        this.operator = Preconditions.checkNotNull(operator);
    }

    @Override
    public Value apply(Value left, Value right) {
        var visitor = new BooleanVisitor();

        left.acceptVisitor(visitor);
        boolean one = visitor.value();

        right.acceptVisitor(visitor);
        boolean two = visitor.value();

        return new BooleanValue(operator.test(one, two));
    }
}
