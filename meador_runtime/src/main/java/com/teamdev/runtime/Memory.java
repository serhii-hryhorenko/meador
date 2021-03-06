package com.teamdev.runtime;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.type.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * A part of a {@link RuntimeEnvironment} that provides access to variables.
 */
public final class Memory {

    private final Map<String, Value> variables = new HashMap<>();

    public Value getVariable(String name) {
        return variables.get(Preconditions.checkNotNull(name));
    }

    public void putVariable(String name, Value value) {
        Preconditions.checkNotNull(name, value);
        variables.put(name, value);
    }

    public void clear() {
        variables.clear();
    }
}
