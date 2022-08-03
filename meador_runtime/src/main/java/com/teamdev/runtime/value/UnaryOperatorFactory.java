package com.teamdev.runtime.value;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.operator.AbstractOperatorFactory;
import com.teamdev.runtime.value.operator.unaryoperator.AbstractUnaryOperator;
import com.teamdev.runtime.value.operator.unaryoperator.BooleanUnaryOperator;
import com.teamdev.runtime.value.operator.unaryoperator.NumericUnaryOperator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Stream;

public class UnaryOperatorFactory implements AbstractOperatorFactory<AbstractUnaryOperator> {

    private final Map<String, AbstractUnaryOperator> unaryOperators = new HashMap<>();

    public UnaryOperatorFactory() {
        unaryOperators.put("++", new NumericUnaryOperator(operand -> ++operand, true));
        unaryOperators.put("--", new NumericUnaryOperator(operand -> --operand, true));
        unaryOperators.put("!", new NumericUnaryOperator(new FactorialOperator()));
        unaryOperators.put("~", new NumericUnaryOperator(operand -> (int) operand));
        unaryOperators.put("±", new NumericUnaryOperator(operand -> -operand));
        unaryOperators.put("not", new BooleanUnaryOperator(operand -> !operand));
    }

    @Override
    public AbstractUnaryOperator create(String operator) {
        return unaryOperators.get(Preconditions.checkNotNull(operator));
    }

    @Override
    public Stream<String> operators() {
        return unaryOperators.keySet().stream();
    }

    @Override
    public boolean hasOperator(String operator) {
        return unaryOperators.containsKey(Preconditions.checkNotNull(operator));
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