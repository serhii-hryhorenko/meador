package com.teamdev.meador.compiler.statement.relative_expr;

import com.teamdev.math.bioperator.AbstractBinaryOperator;
import com.teamdev.math.bioperator.AbstractBinaryOperatorFactory;
import com.teamdev.math.bioperator.RelativeBinaryOperator;

import java.util.HashMap;
import java.util.Map;

import static com.teamdev.math.bioperator.AbstractBinaryOperator.Priority.LOW;

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
