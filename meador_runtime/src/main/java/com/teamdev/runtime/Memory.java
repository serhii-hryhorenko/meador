package com.teamdev.runtime;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.type.Value;
import com.teamdev.runtime.value.type.datastructure.DataStructureTemplate;

import java.util.*;

/**
 * A part of a {@link RuntimeEnvironment} that provides access to variables.
 */
public final class Memory {

    private final Map<String, Value> variables = new HashMap<>();

    private final Set<DataStructureTemplate> dataStructures = new HashSet<>();

    public Value getVariable(String name) {
        return variables.get(Preconditions.checkNotNull(name));
    }

    public void putVariable(String name, Value value) {
        Preconditions.checkNotNull(name, value);
        variables.put(name, value);
    }

    public Optional<DataStructureTemplate> getDataStructureTemplate(String name) {
        Preconditions.checkNotNull(name);

        return dataStructures.stream()
                .filter(structure -> structure.name().equals(name))
                .findFirst();
    }

    public void putDataStructureTemplate(DataStructureTemplate template) {
        Preconditions.checkState(dataStructures.add(Preconditions.checkNotNull(template)),
                "Trying to add an existing structure.");
    }

    public void clear() {
        variables.clear();
    }
}
