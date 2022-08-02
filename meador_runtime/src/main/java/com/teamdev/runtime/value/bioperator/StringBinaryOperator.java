package com.teamdev.runtime.value.bioperator;

import com.teamdev.runtime.value.type.StringValue;
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
        String applied = operator.apply(left.toString(), right.toString());
        System.out.printf("`%s` + `%s` = `%s`\n", left, right, applied);
        return new StringValue(applied);
    }
}
