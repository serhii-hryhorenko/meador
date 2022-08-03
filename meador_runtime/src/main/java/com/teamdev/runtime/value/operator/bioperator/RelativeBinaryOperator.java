package com.teamdev.runtime.value.operator.bioperator;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.type.bool.BooleanValue;
import com.teamdev.runtime.value.type.number.NumericValueVisitor;
import com.teamdev.runtime.value.type.Value;

import java.util.function.BiPredicate;

/**
 * Binary operator from math with validated priority of evaluation.
 */
public class RelativeBinaryOperator extends AbstractBinaryOperator {

    private final BiPredicate<Double, Double> operator;

    public RelativeBinaryOperator(BiPredicate<Double, Double> operator, Priority priority) {
        super(priority);
        this.operator = Preconditions.checkNotNull(operator);
    }

    @Override
    public Value apply(Value a, Value b) {
        var visitor = new NumericValueVisitor();

        a.acceptVisitor(visitor);
        double one = visitor.value();

        b.acceptVisitor(visitor);
        double two = visitor.value();

        return new BooleanValue(operator.test(one, two));
    }

    @Override
    public int hashCode() {
        return operator.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RelativeBinaryOperator &&
                compareTo((RelativeBinaryOperator) o) == 0;
    }
}
