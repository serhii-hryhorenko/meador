package com.teamdev.machine.function;

import com.google.common.base.Preconditions;
import com.teamdev.math.type.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Util output sequence class for collecting interpreted input from {@link FunctionFSM}.
 */
public class FunctionHolder {

    private String functionName;
    private final List<Value> arguments = new ArrayList<>();

    public String functionName() {
        Preconditions.checkState(Objects.nonNull(functionName));
        return functionName;
    }

    void setFunctionName(String functionName) {
        this.functionName = Preconditions.checkNotNull(functionName);
    }

    public List<Value> arguments() {
        return Collections.unmodifiableList(arguments);
    }

    public void addArgument(Value value) {
        arguments.add(Preconditions.checkNotNull(value));
    }
}
