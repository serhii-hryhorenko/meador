package com.teamdev.runtime.operatorfactoryimpl;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.evaluation.operator.AbstractOperatorFactory;
import com.teamdev.runtime.evaluation.operator.AbstractUnaryOperator;
import com.teamdev.runtime.operatorimpl.unaryoperator.BooleanUnaryOperator;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

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
    public Stream<String> operators() {
        return booleanUnaryOperators.keySet()
                .stream();
    }

    @Override
    public boolean hasOperator(String operator) {
        return booleanUnaryOperators.containsKey(Preconditions.checkNotNull(operator));
    }
}
