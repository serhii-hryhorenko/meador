package com.teamdev.meador.programelement.datastructure.fsmimpl;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.evaluation.operandtype.DataStructureTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Output chain for {@link DataStructureTemplate} implementation.
 */
public class DataStructureOutputChain {

    private final List<Command> fieldValues = new ArrayList<>();
    private String templateName;

    public String templateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = Preconditions.checkNotNull(templateName);
    }

    public List<Command> fieldValues() {
        return Collections.unmodifiableList(fieldValues);
    }

    public void addValue(Command value) {
        fieldValues.add(Preconditions.checkNotNull(value));
    }
}
