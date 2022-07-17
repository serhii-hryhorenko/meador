package com.teamdev.meador.compiler.statement.relative_expr;

import com.teamdev.runtime.value.bioperator.AbstractBinaryOperator;
import com.teamdev.runtime.value.bioperator.AbstractBinaryOperatorFactory;
import com.teamdev.runtime.value.bioperator.RelativeBinaryOperator;

import java.util.HashMap;
import java.util.Map;

import static com.teamdev.runtime.value.bioperator.AbstractBinaryOperator.Priority.LOW;


/**
 * {@link AbstractBinaryOperatorFactory} implementation for boolean binary operators
 * that are a part of relational expressions.
 */
public class RelativeBinaryOperatorFactory implements AbstractBinaryOperatorFactory {
    private final Map<String, RelativeBinaryOperator> relativeOperators = new HashMap<>();

    public RelativeBinaryOperatorFactory() {
        relativeOperators.put(">", new RelativeBinaryOperator((left, right) -> left > right, LOW));
        relativeOperators.put("<", new RelativeBinaryOperator((left, right) -> left < right, LOW));
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
        return relativeOperators.keySet().stream().anyMatch(relativeOperator -> relativeOperator.equals(operator));
    }
}
