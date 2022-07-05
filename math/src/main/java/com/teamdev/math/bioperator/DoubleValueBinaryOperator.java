package com.teamdev.math.bioperator;

import com.teamdev.math.type.DoubleValue;
import com.teamdev.math.type.DoubleValueVisitor;
import com.teamdev.math.type.Value;

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
    public Value apply(Value a, Value b) {
        var visitor = new DoubleValueVisitor();

        a.acceptVisitor(visitor);
        double one = visitor.value();

        b.acceptVisitor(visitor);
        double two = visitor.value();

        return new DoubleValue(operator.applyAsDouble(one, two));
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
