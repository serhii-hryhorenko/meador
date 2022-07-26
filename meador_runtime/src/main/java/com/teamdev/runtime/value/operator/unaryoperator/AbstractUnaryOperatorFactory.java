package com.teamdev.runtime.value.operator.unaryoperator;

public interface AbstractUnaryOperatorFactory {

    AbstractUnaryOperator create(String operator);

    boolean acceptOperatorPrefix(String prefix);

    boolean acceptOperator(String operator, Position position);
}
