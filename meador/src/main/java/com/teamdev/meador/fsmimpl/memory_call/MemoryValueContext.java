package com.teamdev.meador.fsmimpl.memory_call;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.Command;

public class MemoryValueContext {

    private String variableName;

    private String fieldName;
    private Command fieldValue;

    public String variableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = Preconditions.checkNotNull(variableName);
    }

    public String fieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = Preconditions.checkNotNull(fieldName);
    }

    public Command command() {
        return fieldValue;
    }

    public void setFieldValue(Command fieldValue) {
        this.fieldValue = Preconditions.checkNotNull(fieldValue);
    }
}
