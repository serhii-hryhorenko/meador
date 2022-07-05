package com.teamdev.math;

import com.teamdev.math.bioperator.DoubleValueBinaryOperator;
import com.teamdev.math.bioperator.PrioritizedBinaryOperatorFactory;

import java.util.HashMap;
import java.util.Map;

import static com.teamdev.math.bioperator.AbstractBinaryOperator.Priority.HIGH;
import static com.teamdev.math.bioperator.AbstractBinaryOperator.Priority.LOW;
import static com.teamdev.math.bioperator.AbstractBinaryOperator.Priority.MEDIUM;

/**
 * {@link PrioritizedBinaryOperatorFactory} implementation with prepared objects inside a Map.
 *
 * Before trying to create an operator strictly recommended to check presence of an operator in
 * implementation.
 *
 * {@code
 * PrioritizedBinaryOperatorFactory factory = new PrioritizedBinaryOperatorFactoryImpl();
 * if (factory.hasOperator(char) {
 * factory.create(char);
 * }
 * }
 */
public class PrioritizedBinaryOperatorFactoryImpl implements PrioritizedBinaryOperatorFactory {

    private final Map<Character, DoubleValueBinaryOperator> operators = new HashMap<>();

    public PrioritizedBinaryOperatorFactoryImpl() {
        operators.put('+', new DoubleValueBinaryOperator(Double::sum, LOW));
        operators.put('-', new DoubleValueBinaryOperator((left, right) -> left - right, LOW));
        operators.put('*', new DoubleValueBinaryOperator((left, right) -> left * right, MEDIUM));
        operators.put('/', new DoubleValueBinaryOperator((left, right) -> left / right, MEDIUM));
        operators.put('^', new DoubleValueBinaryOperator(Math::pow, HIGH));
    }

    @Override
    public DoubleValueBinaryOperator create(char c) {
        return operators.get(c);
    }

    @Override
    public boolean hasOperator(char operator) {
        return operators.containsKey(operator);
    }
}
