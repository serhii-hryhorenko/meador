package com.teamdev.meador.fsmimpl.datastructure;

import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class DataStructureTemplate {

    private String name;
    private final Set<String> fieldNames = new LinkedHashSet<>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataStructureTemplate that))
            return false;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
