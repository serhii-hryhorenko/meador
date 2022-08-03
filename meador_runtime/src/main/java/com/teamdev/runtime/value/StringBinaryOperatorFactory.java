package com.teamdev.runtime.value;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.operator.bioperator.StringBinaryOperator;
import com.teamdev.runtime.value.operator.AbstractOperatorFactory;
import com.teamdev.runtime.value.operator.bioperator.AbstractBinaryOperator;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.teamdev.runtime.value.operator.bioperator.AbstractBinaryOperator.Priority.LOW;

public class StringBinaryOperatorFactory implements AbstractOperatorFactory<AbstractBinaryOperator> {

    private final Map<String, AbstractBinaryOperator> stringOperators = new HashMap<>();

    public StringBinaryOperatorFactory() {
        stringOperators.put("+", new StringBinaryOperator(String::concat, LOW));
    }

    @Override
    public AbstractBinaryOperator create(String operator) {
        return stringOperators.get(Preconditions.checkNotNull(operator));
    }

    @Override
    public Stream<String> operators() {
        return stringOperators.keySet().stream();
    }

    @Override
    public boolean hasOperator(String operator) {
        return stringOperators.containsKey(Preconditions.checkNotNull(operator));
    }
}
