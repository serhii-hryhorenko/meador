package com.teamdev.runtime.value;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.bioperator.AbstractBinaryOperator;
import com.teamdev.runtime.value.bioperator.AbstractBinaryOperatorFactory;
import com.teamdev.runtime.value.bioperator.DoubleValueBinaryOperator;

import java.util.HashMap;
import java.util.Map;

import static com.teamdev.runtime.value.bioperator.AbstractBinaryOperator.Priority.*;

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
        mathOperators.put("+", new DoubleValueBinaryOperator(Double::sum, LOW));
        mathOperators.put("-", new DoubleValueBinaryOperator((left, right) -> left - right, LOW));
        mathOperators.put("*", new DoubleValueBinaryOperator((left, right) -> left * right, MEDIUM));
        mathOperators.put("/", new DoubleValueBinaryOperator((left, right) -> left / right, MEDIUM));
        mathOperators.put("^", new DoubleValueBinaryOperator(Math::pow, HIGH));
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
        return mathOperators.containsKey(Preconditions.checkNotNull(operator));
    }
}
