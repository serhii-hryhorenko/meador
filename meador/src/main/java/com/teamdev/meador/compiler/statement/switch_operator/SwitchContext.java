package com.teamdev.meador.compiler.statement.switch_operator;

import com.google.common.base.Preconditions;
import com.teamdev.meador.runtime.Command;

import java.util.ArrayList;
import java.util.List;

public class SwitchContext {

    private Command valueToMatch;
    private final List<SwitchOptionContext> options = new ArrayList<>();
    private Command defaultCommand;

    public Command value() {
        return valueToMatch;
    }

    public void setValueToMatch(Command valueToMatch) {
        this.valueToMatch = valueToMatch;
    }

    public List<SwitchOptionContext> options() {
        return options;
    }

    public void addOption(SwitchOptionContext option) {
        options.add(Preconditions.checkNotNull(option));
    }

    public Command defaultCommand() {
        return defaultCommand;
    }

    public void setDefaultCommand(Command defaultCommand) {
        this.defaultCommand = Preconditions.checkNotNull(defaultCommand);
    }
}