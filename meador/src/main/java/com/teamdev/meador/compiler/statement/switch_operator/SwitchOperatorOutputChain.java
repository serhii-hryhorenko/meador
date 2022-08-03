package com.teamdev.meador.compiler.statement.switch_operator;

import com.google.common.base.Preconditions;
import com.teamdev.meador.fsmimpl.switch_operator.SwitchOperatorMachine;
import com.teamdev.runtime.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * Output chain for {@link SwitchOperatorMachine}.
 */
public class SwitchOperatorOutputChain {
    private final List<SwitchOptionContext> options = new ArrayList<>();
    private Command valueToMatch;
    private Command defaultCommand;

    public Command value() {
        return Preconditions.checkNotNull(valueToMatch);
    }

    public void setValueToMatch(Command valueToMatch) {
        this.valueToMatch = Preconditions.checkNotNull(valueToMatch);
    }

    public List<SwitchOptionContext> options() {
        return Preconditions.checkNotNull(options);
    }

    public void addOption(SwitchOptionContext option) {
        options.add(Preconditions.checkNotNull(option));
    }

    public Command defaultCommand() {
        return Preconditions.checkNotNull(defaultCommand);
    }

    public void setDefaultCommand(Command defaultCommand) {
        this.defaultCommand = Preconditions.checkNotNull(defaultCommand);
    }
}