package com.teamdev.runtime.value.operator;

/**
 * Factory for creating specified type of Meador operator.
 * Used both for binary and unary operators.
 */
public interface AbstractOperatorFactory<T> {

    T create(String operator);

    boolean acceptOperatorPrefix(String prefix);

    boolean acceptOperator(String operator);
}
