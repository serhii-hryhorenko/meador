package com.teamdev.runtime.value.bioperator;

/**
 * Abstract factory for {@link DoubleValueBinaryOperator}.
 */
public interface AbstractBinaryOperatorFactory {

    AbstractBinaryOperator create(String operator);

    boolean acceptOperatorPrefix(String prefix);

    boolean acceptOperator(String operator);
}
