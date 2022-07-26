package com.teamdev.runtime.value;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.operator.unaryoperator.AbstractUnaryOperator;
import com.teamdev.runtime.value.operator.unaryoperator.AbstractUnaryOperatorFactory;
import com.teamdev.runtime.value.operator.unaryoperator.NumericUnaryOperator;
import com.teamdev.runtime.value.operator.unaryoperator.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

import static com.teamdev.runtime.value.operator.unaryoperator.Position.BOTH;
import static com.teamdev.runtime.value.operator.unaryoperator.Position.PREFIX;

public class UnaryOperatorFactoryImpl implements AbstractUnaryOperatorFactory {

    private final Map<String, AbstractUnaryOperator> unaryOperators = new HashMap<>();

    public UnaryOperatorFactoryImpl() {
        unaryOperators.put("++", new NumericUnaryOperator(operand -> ++operand, BOTH, true));
        unaryOperators.put("--", new NumericUnaryOperator(operand -> --operand, BOTH, true));
        unaryOperators.put("!", new NumericUnaryOperator(new FactorialOperator(), PREFIX));
    }

    @Override
    public AbstractUnaryOperator create(String operator) {
        return unaryOperators.get(Preconditions.checkNotNull(operator));
    }

    @Override
    public boolean acceptOperatorPrefix(String prefix) {
        return unaryOperators.keySet().stream().anyMatch(operator -> operator.startsWith(prefix));
    }

    @Override
    public boolean acceptOperator(String operator, Position position) {
        if (unaryOperators.containsKey(Preconditions.checkNotNull(operator))) {
            Position operatorPosition = unaryOperators.get(operator).position();
            return operatorPosition.equals(position) || operatorPosition.equals(BOTH);
        }

        return false;
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