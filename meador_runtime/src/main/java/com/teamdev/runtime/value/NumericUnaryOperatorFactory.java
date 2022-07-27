package com.teamdev.runtime.value;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.operator.AbstractOperatorFactory;
import com.teamdev.runtime.value.operator.unaryoperator.AbstractUnaryOperator;
import com.teamdev.runtime.value.operator.unaryoperator.NumericUnaryOperator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

public class NumericUnaryOperatorFactory implements AbstractOperatorFactory<AbstractUnaryOperator> {

    private final Map<String, AbstractUnaryOperator> numericUnaryOperators = new HashMap<>();

    public NumericUnaryOperatorFactory() {
        numericUnaryOperators.put("++", new NumericUnaryOperator(operand -> ++operand, true));
        numericUnaryOperators.put("--", new NumericUnaryOperator(operand -> --operand, true));
        numericUnaryOperators.put("!", new NumericUnaryOperator(new FactorialOperator()));
        numericUnaryOperators.put("~", new NumericUnaryOperator(operand -> (int) operand));
    }

    @Override
    public AbstractUnaryOperator create(String operator) {
        return numericUnaryOperators.get(Preconditions.checkNotNull(operator));
    }

    @Override
    public boolean acceptOperatorPrefix(String prefix) {
        return numericUnaryOperators.keySet()
                .stream()
                .anyMatch(operator -> operator.startsWith(Preconditions.checkNotNull(prefix)));
    }

    @Override
    public boolean acceptOperator(String operator) {
        return numericUnaryOperators.containsKey(Preconditions.checkNotNull(operator));
    }

    private static class FactorialOperator implements DoubleUnaryOperator {
        @Override
        public double applyAsDouble(double operand) {
            if (operand < 0) {
                return Double.NaN;
            }

            int factorial = 1;

            for (int i = 2; i <= operand; i++) {
                factorial *= i;
            }

            return factorial;
        }
    }
}