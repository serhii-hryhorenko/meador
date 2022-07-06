package com.teamdev.math.bioperator;


import com.teamdev.math.type.Value;

import java.util.function.BinaryOperator;

public abstract class AbstractBinaryOperator implements BinaryOperator<Value>,
                                                        Comparable<AbstractBinaryOperator> {

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    private final Priority priority;

    AbstractBinaryOperator(Priority priority) {
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
