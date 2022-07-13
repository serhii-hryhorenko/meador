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
    private final Map<Character, RelativeBinaryOperator> relativeOperators = new HashMap<>();

    public RelativeBinaryOperatorFactory() {
        relativeOperators.put('>', new RelativeBinaryOperator((left, right) -> left > right, LOW));
        relativeOperators.put('<', new RelativeBinaryOperator((left, right) -> left < right, LOW));
    }

    @Override
    public AbstractBinaryOperator create(char c) {
        return relativeOperators.get(c);
    }

    @Override
    public boolean hasOperator(char operator) {
        return relativeOperators.containsKey(operator);
    }
}
