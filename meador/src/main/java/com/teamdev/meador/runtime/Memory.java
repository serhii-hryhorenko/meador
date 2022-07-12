package com.teamdev.meador.runtime;

import com.google.common.base.Preconditions;
import com.teamdev.math.type.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A part of a {@link RuntimeEnvironment} that provides access to variables.
 */
public final class Memory {

    private final Map<String, Value> variables = new HashMap<>();

    public Optional<Value> getVariable(String name) {
        return Optional.ofNullable(variables.get(Preconditions.checkNotNull(name)));
    }

    public void putVariable(String name, Value value) {
        Preconditions.checkNotNull(name, value);
        variables.put(name, value);
    }

    public void clear() {
        variables.clear();
    }
}
