package com.teamdev.runtime.evaluation.operandtype;

import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Parsed data structure declaration. Being used for storing field names and quantity of constructor
 * parameters.
 */
public class DataStructureTemplate {

    private final Set<String> fieldNames = new LinkedHashSet<>();
    private String name;

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = Preconditions.checkNotNull(name);
    }

    public void addFieldName(String field) {
        fieldNames.add(Preconditions.checkNotNull(field));
    }

    public Set<String> fieldNames() {
        return Collections.unmodifiableSet(fieldNames);
    }

    public int fieldNumber() {
        return fieldNames.size();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataStructureTemplate)) return false;

        DataStructureTemplate that = (DataStructureTemplate) o;

        if (!fieldNames.equals(that.fieldNames)) return false;
        return name.equals(that.name);
    }
}
