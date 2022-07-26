package com.teamdev.runtime.value;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.operator.bioperator.AbstractBinaryOperator;
import com.teamdev.runtime.value.operator.bioperator.AbstractBinaryOperatorFactory;
import com.teamdev.runtime.value.operator.bioperator.RelativeBinaryOperator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.teamdev.runtime.value.operator.bioperator.AbstractBinaryOperator.Priority.LOW;


/**
 * {@link AbstractBinaryOperatorFactory} implementation for boolean binary operators
 * that are a part of relational expressions.
 */
public class RelativeBinaryOperatorFactory implements AbstractBinaryOperatorFactory {
    private final Map<String, RelativeBinaryOperator> relativeOperators = new HashMap<>();

    public RelativeBinaryOperatorFactory() {
        relativeOperators.put(">", new RelativeBinaryOperator((left, right) -> left > right, LOW));
        relativeOperators.put("<", new RelativeBinaryOperator((left, right) -> left < right, LOW));
        relativeOperators.put(">=", new RelativeBinaryOperator((left, right) -> left >= right, LOW));
        relativeOperators.put("<=", new RelativeBinaryOperator((left, right) -> left <= right, LOW));
        relativeOperators.put("==", new RelativeBinaryOperator(Double::equals, LOW));
        relativeOperators.put("!=", new RelativeBinaryOperator((left, right) -> !Objects.equals(left, right), LOW));
    }

    @Override
    public AbstractBinaryOperator create(String operator) {
        return relativeOperators.get(operator);
    }

    @Override
    public boolean acceptOperatorPrefix(String prefix) {
        return relativeOperators.keySet().stream().anyMatch(operator -> operator.startsWith(prefix));
    }

    @Override
    public boolean acceptOperator(String operator) {
        return relativeOperators.containsKey(Preconditions.checkNotNull(operator));
    }
}
