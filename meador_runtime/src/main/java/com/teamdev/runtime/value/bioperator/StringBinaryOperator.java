package com.teamdev.runtime.value.bioperator;

import com.teamdev.runtime.value.type.StringValue;
import com.teamdev.runtime.value.type.StringVisitor;
import com.teamdev.runtime.value.type.Value;

import java.util.function.BinaryOperator;

public class StringBinaryOperator extends AbstractBinaryOperator {

    private final BinaryOperator<String> operator;

    public StringBinaryOperator(BinaryOperator<String> operator, Priority priority) {
        super(priority);
        this.operator = operator;
    }

    @Override
    public Value apply(Value left, Value right) {
        StringVisitor visitor = new StringVisitor();

        left.acceptVisitor(visitor);
        String leftValue = visitor.value();

        right.acceptVisitor(visitor);
        String rightValue = visitor.value();

        return new StringValue(operator.apply(leftValue, rightValue));
    }
}
