package com.teamdev.runtime.value;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.bioperator.AbstractBinaryOperator;
import com.teamdev.runtime.value.bioperator.AbstractBinaryOperatorFactory;
import com.teamdev.runtime.value.bioperator.BooleanValueBinaryOperator;

import java.util.HashMap;
import java.util.Map;

import static com.teamdev.runtime.value.bioperator.AbstractBinaryOperator.Priority.LOW;

public class BooleanBinaryOperatorFactory implements AbstractBinaryOperatorFactory {

    private final Map<String, BooleanValueBinaryOperator> booleanOperators = new HashMap<>();

    public BooleanBinaryOperatorFactory() {
        booleanOperators.put("&&", new BooleanValueBinaryOperator((left, right) -> left && right, LOW));
        booleanOperators.put("||", new BooleanValueBinaryOperator((left, right) -> left || right, LOW));
    }

    @Override

    public AbstractBinaryOperator create(String operator) {
        Preconditions.checkState(acceptOperator(Preconditions.checkNotNull(operator)));
        return booleanOperators.get(operator);
    }

    @Override
    public boolean acceptOperatorPrefix(String prefix) {
        Preconditions.checkNotNull(prefix);
        return booleanOperators.keySet().stream().anyMatch(boolOperator -> boolOperator.startsWith(prefix));
    }

    @Override
    public boolean acceptOperator(String operator) {
        Preconditions.checkNotNull(operator);
        return booleanOperators.keySet().stream().anyMatch(boolOperator -> boolOperator.equals(operator));
    }
}
