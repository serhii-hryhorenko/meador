package com.teamdev.meador.compiler;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.Program;
import com.teamdev.meador.ProgramElementCompilerFactoryImpl;
import com.teamdev.meador.fsmimpl.compiler.CompilerMachine;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.teamdev.meador.compiler.ProgramElement.LIST_OF_STATEMENTS;

/**
 * Compiles {@link Program} into a single runtime {@link Command}.
 */
public class Compiler {
    public Optional<Command> compile(Program program) throws CompilingException {

        var reader = new InputSequenceReader(Preconditions.checkNotNull(program).getCode());

        ProgramElementCompilerFactory factory = new ProgramElementCompilerFactoryImpl();

        var optionalProgram = factory.create(LIST_OF_STATEMENTS).compile(reader);

        if (reader.canRead()) {
            throw new CompilingException("Program compilation failed.");
        }

        return optionalProgram;
    }
}
