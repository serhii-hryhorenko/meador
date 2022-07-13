package com.teamdev.meador.fsmimpl.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.Command;

public class FieldAssignmentContext {

    private String structureName;
    private String fieldName;

    private Command value;

    public String structureName() {
        return structureName;
    }

    public void setStructureName(String structureName) {
        this.structureName = Preconditions.checkNotNull(structureName);
    }

    public String fieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = Preconditions.checkNotNull(fieldName);
    }

    public Command command() {
        return value;
    }

    public void setValue(Command value) {
        this.value = Preconditions.checkNotNull(value);
    }
}
