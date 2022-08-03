package com.teamdev.runtime.value;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.operator.AbstractOperatorFactory;
import com.teamdev.runtime.value.operator.bioperator.AbstractBinaryOperator;
import com.teamdev.runtime.value.operator.bioperator.RelativeBinaryOperator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.teamdev.runtime.value.operator.bioperator.AbstractBinaryOperator.Priority.LOW;


/**
 * {@link AbstractOperatorFactory} implementation for boolean binary operators
 * that are a part of relational expressions.
 */
public class RelativeBinaryOperatorFactory implements AbstractOperatorFactory<AbstractBinaryOperator> {
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
    public Stream<String> operators() {
        return relativeOperators.keySet().stream();
    }

    @Override
    public boolean hasOperator(String operator) {
        return relativeOperators.containsKey(Preconditions.checkNotNull(operator));
    }
}
