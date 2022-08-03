package com.teamdev.runtime.value;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.operator.bioperator.BooleanValueBinaryOperator;
import com.teamdev.runtime.value.operator.AbstractOperatorFactory;
import com.teamdev.runtime.value.operator.bioperator.AbstractBinaryOperator;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.teamdev.runtime.value.operator.bioperator.AbstractBinaryOperator.Priority.LOW;
import static com.teamdev.runtime.value.operator.bioperator.AbstractBinaryOperator.Priority.MEDIUM;

public class BooleanBinaryOperatorFactory implements AbstractOperatorFactory<AbstractBinaryOperator> {

    private final Map<String, BooleanValueBinaryOperator> booleanOperators = new HashMap<>();

    public BooleanBinaryOperatorFactory() {
        booleanOperators.put("&&", new BooleanValueBinaryOperator((left, right) -> left && right, MEDIUM));
        booleanOperators.put("||", new BooleanValueBinaryOperator((left, right) -> left || right, LOW));
        booleanOperators.put("^", new BooleanValueBinaryOperator((left, right) -> left ^ right, MEDIUM));
    }

    @Override
    public AbstractBinaryOperator create(String operator) {
        Preconditions.checkState(hasOperator(Preconditions.checkNotNull(operator)));
        return booleanOperators.get(operator);
    }

    @Override
    public Stream<String> operators() {
        return booleanOperators.keySet().stream();
    }


    @Override
    public boolean hasOperator(String operator) {
        Preconditions.checkNotNull(operator);
        return booleanOperators.keySet().stream().anyMatch(boolOperator -> boolOperator.equals(operator));
    }
}
