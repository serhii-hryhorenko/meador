package com.teamdev.runtime.operatorimpl.bioperator;

import com.teamdev.runtime.evaluation.operandtype.StringValue;
import com.teamdev.runtime.evaluation.operandtype.Value;
import com.teamdev.runtime.evaluation.operator.AbstractBinaryOperator;

import java.util.function.BinaryOperator;

public class StringBinaryOperator extends AbstractBinaryOperator {

    private final BinaryOperator<String> operator;

    public StringBinaryOperator(BinaryOperator<String> operator,
                                AbstractBinaryOperator.Priority priority) {
        super(priority);
        this.operator = operator;
    }

    @Override
    public Value apply(Value left, Value right) {
        return new StringValue(operator.apply(left.toString(), right.toString()));
    }
}
