package com.teamdev.runtime.value.type.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.variable.VariableHolder;

import java.util.*;

/**
 * Data structure implementation with a set of variables with predeclared by the {@link DataStructureTemplate}
 * names. Fields are lazy evaluated and stored as {@link Command}.
 */
public class DataStructureHolder {

    private final Iterator<String> fieldIterator;

    private final String type;
    private final Set<VariableHolder> fields = new LinkedHashSet<>();

    public DataStructureHolder(DataStructureTemplate template) {
        this.type = template.name();
        this.fieldIterator = Preconditions.checkNotNull(template).fieldNames().iterator();
    }

    public void assignFieldValue(Command value) {
        if (fieldIterator.hasNext()) {
            fields.add(new VariableHolder()
                    .setName(fieldIterator.next())
                    .setCommand(Preconditions.checkNotNull(value)));

            return;
        }

        throw new IllegalStateException("All fields are already initialized.");
    }

    public void assignFieldValue(String fieldName, Command value) {
        Preconditions.checkNotNull(fieldName, value);

        var optionalVariable = getField(fieldName);

        optionalVariable.ifPresent(variable -> variable.setCommand(value));

    }

    public Optional<VariableHolder> getField(String fieldName) {
        return fields.stream()
                .filter(variable -> variable.name().equals(Preconditions.checkNotNull(fieldName)))
                .findFirst();
    }

    @Override
    public String toString() {
        return type + '{' +
                "fields=" + Arrays.toString(fields.stream().map(VariableHolder::name).toArray()) +
                '}';
    }
}
