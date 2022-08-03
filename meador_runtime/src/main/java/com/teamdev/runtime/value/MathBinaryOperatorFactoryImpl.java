package com.teamdev.runtime.value;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.operator.AbstractOperatorFactory;
import com.teamdev.runtime.value.operator.bioperator.AbstractBinaryOperator;
import com.teamdev.runtime.value.operator.bioperator.NumericValueBinaryOperator;
import com.teamdev.runtime.value.type.number.NumericValue;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.teamdev.runtime.value.operator.bioperator.AbstractBinaryOperator.Priority.*;

/**
 * {@link AbstractOperatorFactory} implementation with prepared objects inside a Map.
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
public class MathBinaryOperatorFactoryImpl implements AbstractOperatorFactory<AbstractBinaryOperator> {

    private final Map<String, AbstractBinaryOperator> mathOperators = new HashMap<>();

    public MathBinaryOperatorFactoryImpl() {
        mathOperators.put("+", new NumericValueBinaryOperator(Double::sum, LOW));
        mathOperators.put("-", new NumericValueBinaryOperator((left, right) -> left - right, LOW));
        mathOperators.put("*", new NumericValueBinaryOperator((left, right) -> left * right, MEDIUM));
        mathOperators.put("/", new NumericValueBinaryOperator((left, right) -> left / right, MEDIUM));
        mathOperators.put("^", new NumericValueBinaryOperator(Math::pow, HIGH));
    }

    @Override
    public AbstractBinaryOperator create(String operator) {
        return mathOperators.get(operator);
    }

    @Override
    public Stream<String> operators() {
        return mathOperators.keySet().stream();
    }

    @Override
    public boolean hasOperator(String operator) {
        return mathOperators.containsKey(Preconditions.checkNotNull(operator));
    }
}
