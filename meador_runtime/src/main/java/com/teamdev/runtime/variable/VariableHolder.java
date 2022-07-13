package com.teamdev.runtime.variable;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.Command;

import java.util.Objects;

/**
 * Runtime holder for a Meador variable.
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

    public void setName(String name) {
        this.name = Preconditions.checkNotNull(name);
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
