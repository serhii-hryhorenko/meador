package com.teamdev.machine.function;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Output chain for {@link FunctionMachine}.
 */
public class FunctionHolder<T> {

    private final List<T> arguments = new ArrayList<>();
    private String functionName;

    public String functionName() {
        Preconditions.checkState(Objects.nonNull(functionName));
        return functionName;
    }

    void setFunctionName(String functionName) {
        this.functionName = Preconditions.checkNotNull(functionName);
    }

    public List<T> arguments() {
        return Collections.unmodifiableList(arguments);
    }

    public void addArgument(T value) {
        arguments.add(Preconditions.checkNotNull(value));
    }
}
