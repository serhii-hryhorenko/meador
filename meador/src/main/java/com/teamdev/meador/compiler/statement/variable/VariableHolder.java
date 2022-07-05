package com.teamdev.meador.compiler.statement.variable;

import com.google.common.base.Preconditions;
import com.teamdev.meador.runtime.Command;

public class VariableHolder {

    private String name;
    private Command command;

    public String name() {
        return name;
    }

    public Command command() {
        return command;
    }

    void setName(String name) {
        this.name = Preconditions.checkNotNull(name);
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
