package com.teamdev.meador.fsmimpl.datastructure;

import com.google.common.base.Preconditions;

public class FieldReferenceOutputChain {
    private String variableName;
    private String fieldName;

    public String variableName() {
        return Preconditions.checkNotNull(variableName);
    }

    public void setVariableName(String variableName) {
        this.variableName = Preconditions.checkNotNull(variableName);
    }

    public String fieldName() {
        return Preconditions.checkNotNull(fieldName);
    }

    public void setFieldName(String fieldName) {
        this.fieldName = Preconditions.checkNotNull(fieldName);
    }
}
