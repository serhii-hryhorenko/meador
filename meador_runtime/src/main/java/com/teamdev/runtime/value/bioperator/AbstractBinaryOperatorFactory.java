package com.teamdev.runtime.value.bioperator;

/**
 * Abstract factory for {@link DoubleValueBinaryOperator}.
 */
public interface AbstractBinaryOperatorFactory {

    AbstractBinaryOperator create(char c);

    boolean hasOperator(char operator);
}
