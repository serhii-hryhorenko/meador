package com.teamdev.runtime.value.operator.bioperator;


import com.teamdev.runtime.value.type.Value;

import java.util.function.BinaryOperator;

public abstract class AbstractBinaryOperator implements BinaryOperator<Value>,
        Comparable<AbstractBinaryOperator> {

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    private final Priority priority;

    protected AbstractBinaryOperator(Priority priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(AbstractBinaryOperator o) {
        return priority.compareTo(o.priority);
    }

    Priority priority() {
        return priority;
    }
}
