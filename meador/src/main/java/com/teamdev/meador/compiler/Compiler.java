package com.teamdev.meador.compiler;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequence;
import com.teamdev.meador.Program;
import com.teamdev.meador.StatementCompilerFactoryImpl;
import com.teamdev.meador.runtime.Command;

import java.util.Optional;

/**
 * Compiles {@link Program} into a single runtime {@link Command}.
 */
public class Compiler {
    public Optional<Command> compile(Program program) throws CompilingException {

        var inputSequence = new InputSequence(Preconditions.checkNotNull(program).getCode());

        StatementCompilerFactory factory = new StatementCompilerFactoryImpl();

        var compiler = factory.create(StatementType.PROGRAM);

        Optional<Command> optionalCommand = compiler.compile(inputSequence);

        if (inputSequence.canRead()) {
            throw new CompilingException();
        }

        return optionalCommand;
    }
}
