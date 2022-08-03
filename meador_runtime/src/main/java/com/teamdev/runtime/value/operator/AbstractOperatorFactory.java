package com.teamdev.runtime.value.operator;

import java.util.stream.Stream;

/**
 * Factory for creating specified type of Meador operator.
 * Used both for binary and unary operators.
 */
public interface AbstractOperatorFactory<T> {

    T create(String operator);

    Stream<String> operators();

    boolean hasOperator(String operator);
}
