package com.teamdev.runtime.value.bioperator;

import com.teamdev.runtime.value.type.DoubleValue;
import com.teamdev.runtime.value.type.DoubleValueVisitor;
import com.teamdev.runtime.value.type.StringValue;
import com.teamdev.runtime.value.type.Value;

import java.util.function.DoubleBinaryOperator;

/**
 * Binary operator from math with validated priority of evaluation.
 */
public class DoubleValueBinaryOperator extends AbstractBinaryOperator {

    private final DoubleBinaryOperator operator;

    public DoubleValueBinaryOperator(DoubleBinaryOperator operator, Priority priority) {
        super(priority);
        this.operator = operator;
    }

    @Override
    public Value apply(Value left, Value right) {

        var visitor = new DoubleValueVisitor();

        try {
            left.acceptVisitor(visitor);
            double leftValue = visitor.value();

            right.acceptVisitor(visitor);
            double rightValue = visitor.value();

            return new DoubleValue(operator.applyAsDouble(leftValue, rightValue));
        } catch (IllegalArgumentException ile) {
            return new StringValue(left + right.toString());
        }
    }

    @Override
    public int hashCode() {
        var result = operator.hashCode();
        result = 31 * result + priority().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DoubleValueBinaryOperator &&
                compareTo((DoubleValueBinaryOperator) o) == 0;
    }
}
