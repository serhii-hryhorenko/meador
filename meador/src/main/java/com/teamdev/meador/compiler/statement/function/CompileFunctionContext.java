package com.teamdev.meador.compiler.statement.function;

import com.google.common.base.Preconditions;
import com.teamdev.machine.function.FunctionHolder;
import com.teamdev.meador.runtime.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompileFunctionContext extends FunctionHolder {

    private final List<Command> commands = new ArrayList<>();

    public CompileFunctionContext() {
        super();
    }

    public void addCommand(Command command) {
        commands.add(Preconditions.checkNotNull(command));
    }

    public List<Command> commands() {
        return Collections.unmodifiableList(commands);
    }
}
