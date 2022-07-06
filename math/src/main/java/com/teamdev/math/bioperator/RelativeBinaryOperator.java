package com.teamdev.math.bioperator;

import com.google.common.base.Preconditions;
import com.teamdev.math.type.BooleanValue;
import com.teamdev.math.type.DoubleValue;
import com.teamdev.math.type.DoubleValueVisitor;
import com.teamdev.math.type.Value;

import java.util.function.BiFunction;
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
        var visitor = new DoubleValueVisitor();

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
