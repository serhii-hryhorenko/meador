package com.teamdev.runtime.evaluation.operator;

import com.teamdev.runtime.evaluation.TypeMismatchException;
import com.teamdev.runtime.evaluation.operandtype.Value;

public abstract class AbstractBinaryOperator implements Comparable<AbstractBinaryOperator> {

    private final Priority priority;

    public abstract Value apply(Value left, Value right) throws TypeMismatchException;

    protected AbstractBinaryOperator(Priority priority) {
        this.priority = priority;
    }

    protected Priority priority() {
        return priority;
    }

    @Override
    public int compareTo(AbstractBinaryOperator o) {
        return priority.compareTo(o.priority);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AbstractBinaryOperator && compareTo((AbstractBinaryOperator) o) == 0;
    }

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
    }
}
