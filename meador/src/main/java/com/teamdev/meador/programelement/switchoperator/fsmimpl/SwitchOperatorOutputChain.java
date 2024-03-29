package com.teamdev.meador.programelement.switchoperator.fsmimpl;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * Output chain for {@link SwitchOperatorMachine}.
 */
public class SwitchOperatorOutputChain {
    private final List<CaseOptionOutputChain> options = new ArrayList<>();
    private Command mappedValue;
    private Command defaultCommand;

    public Command mappedValue() {
        return Preconditions.checkNotNull(mappedValue);
    }

    public void setMappedValue(Command mappedValue) {
        this.mappedValue = Preconditions.checkNotNull(mappedValue);
    }

    public List<CaseOptionOutputChain> options() {
        return Preconditions.checkNotNull(options);
    }

    public void addOption(CaseOptionOutputChain option) {
        options.add(Preconditions.checkNotNull(option));
    }

    public Command defaultCommand() {
        return Preconditions.checkNotNull(defaultCommand);
    }

    public void setDefaultCommand(Command defaultCommand) {
        this.defaultCommand = Preconditions.checkNotNull(defaultCommand);
    }
}