package com.teamdev.runtime.value.operator.bioperator;

import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.value.type.number.NumericValue;
import com.teamdev.runtime.value.type.number.NumericValueVisitor;
import com.teamdev.runtime.value.type.string.StringValue;
import com.teamdev.runtime.value.type.Value;

import java.util.function.DoubleBinaryOperator;

/**
 * Binary operator from math with validated priority of evaluation.
 */
public class NumericValueBinaryOperator extends AbstractBinaryOperator {

    private final DoubleBinaryOperator operator;

    public NumericValueBinaryOperator(DoubleBinaryOperator operator, Priority priority) {
        super(priority);
        this.operator = operator;
    }

    @Override
    public Value apply(Value left, Value right) throws MeadorRuntimeException {

        var visitor = new NumericValueVisitor();

        left.acceptVisitor(visitor);
        double leftValue = visitor.value();

        right.acceptVisitor(visitor);
        double rightValue = visitor.value();

        return new NumericValue(operator.applyAsDouble(leftValue, rightValue));
    }

    @Override
    public int hashCode() {
        var result = operator.hashCode();
        result = 31 * result + priority().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NumericValueBinaryOperator &&
                compareTo((NumericValueBinaryOperator) o) == 0;
    }
}
