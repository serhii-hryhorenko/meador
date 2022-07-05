package com.teamdev.math.bioperator;


import com.teamdev.math.type.Value;

import java.util.function.BiFunction;

public abstract class AbstractBinaryOperator implements BiFunction<Value, Value, Value>,
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
    public abstract Value apply(Value a, Value b);

    @Override
    public int compareTo(AbstractBinaryOperator o) {
        return priority.compareTo(o.priority);
    }

    Priority priority() {
        return priority;
    }
}
