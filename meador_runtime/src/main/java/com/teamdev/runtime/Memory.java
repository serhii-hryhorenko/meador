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

    public Value getVariable(String name) throws MeadorRuntimeException {
        if (!variables.containsKey(Preconditions.checkNotNull(name))) {
            throw new MeadorRuntimeException(String.format("`%s` variable is not initialized.", name));
        }

        return variables.get(name);
    }

    public void putVariable(String name, Value value) {
        Preconditions.checkNotNull(name, value);
        variables.put(name, value);
    }

    public DataStructureTemplate getDataStructureTemplate(String name) throws MeadorRuntimeException {
        var optionalTemplate = dataStructures.stream()
                .filter(structure -> structure.name().equals(name))
                .findFirst();

        if (optionalTemplate.isEmpty()) {
            throw new MeadorRuntimeException(String.format("`%s` structure is not initialized.", name));
        }

        return optionalTemplate.get();
    }

    public void putDataStructureTemplate(DataStructureTemplate template) throws MeadorRuntimeException {
        if (!dataStructures.add(Preconditions.checkNotNull(template))) {
            throw new MeadorRuntimeException(String.format("`%s` template is already exists.", template.name()));
        }
    }

    public void clear() {
        variables.clear();
    }
}
