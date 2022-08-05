package com.teamdev.runtime.evaluation.operandtype;

import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Data structure implementation with a set of variables with predeclared by the {@link
 * DataStructureTemplate} names.
 */
public class DataStructureHolder {

    private final Iterator<String> fieldIterator;

    private final String type;
    private final Map<String, Value> fields = new HashMap<>();

    public DataStructureHolder(DataStructureTemplate template) {
        this.type = template.name();
        this.fieldIterator = Preconditions.checkNotNull(template)
                                          .fieldNames()
                                          .iterator();
    }

    public void assignFieldValue(Value value) {
        if (fieldIterator.hasNext()) {
            fields.put(fieldIterator.next(), value);
            return;
        }

        throw new IllegalStateException("All fields are already initialized.");
    }

    public void assignFieldValue(String name, Value value) {
        Preconditions.checkNotNull(name, value);
        getField(name).setValue(value);
    }

    public Map.Entry<String, Value> getField(String fieldName) {
        var optionalField = fields.entrySet().stream()
                .filter(variable -> variable.getKey()
                                            .equals(Preconditions.checkNotNull(fieldName)))
                .findFirst();

        if (optionalField.isEmpty()) {
            throw new IllegalStateException(
                    String.format("There is no field with name `%s` in %s", fieldName, type));
        }

        return optionalField.get();
    }

    @Override
    public String toString() {
        return type + "{fields=" + Arrays.toString(fields.keySet().toArray()) + '}';
    }
}
