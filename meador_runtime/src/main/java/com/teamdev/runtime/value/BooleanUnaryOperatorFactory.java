package com.teamdev.runtime.value;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.operator.AbstractOperatorFactory;
import com.teamdev.runtime.value.operator.unaryoperator.AbstractUnaryOperator;
import com.teamdev.runtime.value.operator.unaryoperator.BooleanUnaryOperator;

import java.util.HashMap;
import java.util.Map;

public class BooleanUnaryOperatorFactory implements AbstractOperatorFactory<AbstractUnaryOperator> {

    private final Map<String, BooleanUnaryOperator> booleanUnaryOperators = new HashMap<>();

    public BooleanUnaryOperatorFactory() {
        booleanUnaryOperators.put("!", new BooleanUnaryOperator(aBoolean -> !aBoolean));
    }

    @Override
    public AbstractUnaryOperator create(String operator) {
        return booleanUnaryOperators.get(Preconditions.checkNotNull(operator));
    }

    @Override
    public boolean acceptOperatorPrefix(String prefix) {
        return booleanUnaryOperators.keySet()
                .stream()
                .anyMatch(operator -> operator.startsWith(Preconditions.checkNotNull(prefix)));
    }

    @Override
    public boolean acceptOperator(String operator) {
        return booleanUnaryOperators.containsKey(Preconditions.checkNotNull(operator));
    }
}
