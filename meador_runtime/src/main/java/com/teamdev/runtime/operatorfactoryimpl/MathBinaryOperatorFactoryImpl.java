package com.teamdev.runtime.operatorfactoryimpl;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.evaluation.operator.AbstractBinaryOperator;
import com.teamdev.runtime.evaluation.operator.AbstractOperatorFactory;
import com.teamdev.runtime.operatorimpl.bioperator.NumericValueBinaryOperator;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.teamdev.runtime.evaluation.operator.AbstractBinaryOperator.Priority.HIGH;
import static com.teamdev.runtime.evaluation.operator.AbstractBinaryOperator.Priority.LOW;
import static com.teamdev.runtime.evaluation.operator.AbstractBinaryOperator.Priority.MEDIUM;

/**
 * {@link AbstractOperatorFactory} implementation with prepared objects inside a Map.
 * <p>
 * Before trying to create an operator strictly recommended to check presence of an operator in
 * implementation.
 * <pre>
 * PrioritizedBinaryOperatorFactory factory = new PrioritizedBinaryOperatorFactoryImpl();
 *
 * if (factory.hasOperator(operator) {
 *      factory.create(char);
 * }
 * </pre>
 */
public class MathBinaryOperatorFactoryImpl implements AbstractOperatorFactory<AbstractBinaryOperator> {

    private final Map<String, AbstractBinaryOperator> mathOperators = new HashMap<>();

    public MathBinaryOperatorFactoryImpl() {
        mathOperators.put("+", new NumericValueBinaryOperator(Double::sum, LOW));
        mathOperators.put("-", new NumericValueBinaryOperator((left, right) -> left - right, LOW));
        mathOperators.put("*",
                          new NumericValueBinaryOperator((left, right) -> left * right, MEDIUM));
        mathOperators.put("/",
                          new NumericValueBinaryOperator((left, right) -> left / right, MEDIUM));
        mathOperators.put("^", new NumericValueBinaryOperator(Math::pow, HIGH));
    }

    @Override
    public AbstractBinaryOperator create(String operator) {
        return mathOperators.get(operator);
    }

    @Override
    public Stream<String> operators() {
        return mathOperators.keySet()
                .stream();
    }

    @Override
    public boolean hasOperator(String operator) {
        return mathOperators.containsKey(Preconditions.checkNotNull(operator));
    }
}
