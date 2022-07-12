package com.teamdev.meador.fsmimpl.variable;

import com.google.common.base.Preconditions;
import com.teamdev.meador.runtime.Command;

import java.util.Objects;

/**
 * Output chain for {@link VariableDeclarationFSM}.
 */
public class VariableHolder {

    private String name;
    private Command command;

    public String name() {
        Preconditions.checkState(Objects.nonNull(name));
        return name;
    }

    public Command command() {
        Preconditions.checkState(Objects.nonNull(command));
        return command;
    }

    public VariableHolder setName(String name) {
        this.name = Preconditions.checkNotNull(name);
        return this;
    }

    public VariableHolder setCommand(Command command) {
        this.command = command;
        return this;
    }
}
