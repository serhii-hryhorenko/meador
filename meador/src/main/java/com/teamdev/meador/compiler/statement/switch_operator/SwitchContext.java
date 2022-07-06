package com.teamdev.meador.compiler.statement.switch_operator;

import com.google.common.base.Preconditions;
import com.teamdev.meador.runtime.Command;

import java.util.ArrayList;
import java.util.List;

public class SwitchContext {

    private Command value;
    private final List<SwitchOptionContext> options = new ArrayList<>();

    public Command value() {
        return value;
    }

    public void setValue(Command value) {
        this.value = value;
    }

    public List<SwitchOptionContext> options() {
        return options;
    }

    public void addOption(SwitchOptionContext option) {
        options.add(Preconditions.checkNotNull(option));
    }
}