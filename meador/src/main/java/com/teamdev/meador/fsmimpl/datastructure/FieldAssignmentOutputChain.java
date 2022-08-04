package com.teamdev.meador.fsmimpl.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.Command;

/**
 * Output chain for {@link FieldAssignmentMachine}.
 */
public class FieldAssignmentOutputChain {
    private FieldReferenceOutputChain field;
    private Command command;

    public FieldReferenceOutputChain field() {
        return Preconditions.checkNotNull(field);
    }

    public void setField(FieldReferenceOutputChain field) {
        this.field = field;
    }

    public Command command() {
        return Preconditions.checkNotNull(command);
    }

    public void setCommand(Command command) {
        this.command = Preconditions.checkNotNull(command);
    }
}
