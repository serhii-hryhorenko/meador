package com.teamdev.runtime.value.type.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.variable.VariableHolder;

import java.util.*;
import java.util.stream.Stream;

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

    public void assignFieldValue(Command value) throws MeadorRuntimeException {
        if (fieldIterator.hasNext()) {
            fields.add(new VariableHolder()
                    .setName(fieldIterator.next())
                    .setCommand(Preconditions.checkNotNull(value)));

            return;
        }

        throw new MeadorRuntimeException("All fields are already initialized.");
    }

    public void assignFieldValue(String fieldName, Command command) throws MeadorRuntimeException {
        Preconditions.checkNotNull(fieldName, command);
        getField(fieldName).setCommand(command);
    }

    public VariableHolder getField(String fieldName) throws MeadorRuntimeException {
        var optionalField = fields.stream()
                .filter(variable -> variable.name().equals(Preconditions.checkNotNull(fieldName)))
                .findFirst();

        if (optionalField.isEmpty()) {
            throw new MeadorRuntimeException(String.format("There is no field with name `%s` in %s", fieldName, type));
        }

        return optionalField.get();
    }

    @Override
    public String toString() {
        return type + '{' +
                "fields=" + Arrays.toString(fields.stream().map(VariableHolder::name).toArray()) +
                '}';
    }
}
