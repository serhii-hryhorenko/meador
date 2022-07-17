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

    private final Map<String, DoubleValueBinaryOperator> mathOperators = new HashMap<>();

    public MathBinaryOperatorFactoryImpl() {
        mathOperators.put("+", new DoubleValueBinaryOperator(Double::sum, AbstractBinaryOperator.Priority.LOW));
        mathOperators.put("-", new DoubleValueBinaryOperator((left, right) -> left - right, AbstractBinaryOperator.Priority.LOW));
        mathOperators.put("*", new DoubleValueBinaryOperator((left, right) -> left * right, AbstractBinaryOperator.Priority.MEDIUM));
        mathOperators.put("/", new DoubleValueBinaryOperator((left, right) -> left / right, AbstractBinaryOperator.Priority.MEDIUM));
        mathOperators.put("^", new DoubleValueBinaryOperator(Math::pow, AbstractBinaryOperator.Priority.HIGH));
    }

    @Override
    public AbstractBinaryOperator create(String operator) {
        return mathOperators.get(operator);
    }

    @Override
    public boolean acceptOperatorPrefix(String prefix) {
        return mathOperators.keySet().stream().anyMatch(operator -> operator.startsWith(prefix));
    }

    @Override
    public boolean acceptOperator(String operator) {
        return mathOperators.keySet().stream().anyMatch(mathOperator -> mathOperator.equals(operator));
    }
}
