package com.teamdev.math.bioperator;

/**
 * Abstract factory for {@link DoubleValueBinaryOperator}.
 */
public interface PrioritizedBinaryOperatorFactory {

    DoubleValueBinaryOperator create(char c);

    boolean hasOperator(char operator);
}
