package com.teamdev.runtime.value;

import com.teamdev.runtime.value.bioperator.AbstractBinaryOperator;
import com.teamdev.runtime.value.bioperator.AbstractBinaryOperatorFactory;
import com.teamdev.runtime.value.bioperator.DoubleValueBinaryOperator;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link AbstractBinaryOperatorFactory} implementation with prepared objects inside a Map.
 * <p>
 * Before trying to create an operator strictly recommended to check presence of an operator in
 * implementation.
 * <p>
 * {@code
 * PrioritizedBinaryOperatorFactory factory = new PrioritizedBinaryOperatorFactoryImpl();
 * if (factory.hasOperator(char) {
 * factory.create(char);
 * }
 * }
 */
public class MathBinaryOperatorFactoryImpl implements AbstractBinaryOperatorFactory {

    private final Map<Character, DoubleValueBinaryOperator> operators = new HashMap<>();

    public MathBinaryOperatorFactoryImpl() {
        operators.put('+', new DoubleValueBinaryOperator(Double::sum, AbstractBinaryOperator.Priority.LOW));
        operators.put('-', new DoubleValueBinaryOperator((left, right) -> left - right, AbstractBinaryOperator.Priority.LOW));
        operators.put('*', new DoubleValueBinaryOperator((left, right) -> left * right, AbstractBinaryOperator.Priority.MEDIUM));
        operators.put('/', new DoubleValueBinaryOperator((left, right) -> left / right, AbstractBinaryOperator.Priority.MEDIUM));
        operators.put('^', new DoubleValueBinaryOperator(Math::pow, AbstractBinaryOperator.Priority.HIGH));
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
