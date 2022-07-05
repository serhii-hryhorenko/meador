package com.teamdev.meador.compiler;

import com.teamdev.fsm.InputSequence;
import com.teamdev.meador.Program;
import com.teamdev.meador.compiler.fsmimpl.CompilerFSM;
import com.teamdev.meador.runtime.Command;

import java.util.ArrayList;
import java.util.Optional;

public class Compiler {

    public Optional<Command> compile(Program program) throws CompilingException {

        var inputSequence = new InputSequence(program.getCode());

        StatementCompilerFactory factory = new StatementCompilerFactoryImpl();

        var compiler = (StatementCompiler) compilerInput -> {
            var commands = new ArrayList<Command>();

            if (CompilerFSM.create(factory)
                           .accept(compilerInput, commands)) {

                return Optional.of(
                        runtimeEnvironment -> commands.forEach(
                                command -> command.execute(runtimeEnvironment)));
            }

            return Optional.empty();
        };

        return compiler.compile(inputSequence);
    }
}
