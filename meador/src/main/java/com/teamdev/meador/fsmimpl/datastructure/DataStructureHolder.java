package com.teamdev.meador.fsmimpl.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.meador.fsmimpl.variable.VariableHolder;
import com.teamdev.meador.runtime.Command;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class DataStructureHolder {

    private final Iterator<String> fieldIterator;
    private final Set<VariableHolder> fields = new HashSet<>();

    public DataStructureHolder(DataStructureTemplate template) {
        this.fieldIterator = Preconditions.checkNotNull(template).fields().iterator();
    }

    public void assignFieldValue(Command value) {
        if (fieldIterator.hasNext()) {
            fields.add(new VariableHolder()
                    .setName(fieldIterator.next())
                    .setCommand(Preconditions.checkNotNull(value)));
        }

    }

    public boolean assignFieldValue(String fieldName, Command value) {
        Preconditions.checkNotNull(fieldName, value);

        var optionalVariable = getField(fieldName);

        optionalVariable.ifPresent(variable -> variable.setCommand(value));

        return optionalVariable.isPresent();
    }

    public boolean isFieldPresent(String fieldName) {
        return getField(fieldName).isPresent();
    }

    public Optional<Command> getFieldValue(String fieldName) {
        return getField(fieldName).map(VariableHolder::command);
    }

    public Optional<VariableHolder> getField(String fieldName) {
        return fields.stream()
                .filter(variable -> variable.name().equals(Preconditions.checkNotNull(fieldName)))
                .findFirst();
    }
}
