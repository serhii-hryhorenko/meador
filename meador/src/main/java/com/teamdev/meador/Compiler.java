package com.teamdev.meador;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.programelement.CompilingException;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.runtime.Command;

import java.util.Optional;

import static com.teamdev.meador.programelement.ProgramElement.LIST_OF_STATEMENTS;

/**
 * Compiles {@link Program} into a single runtime {@link Command}.
 */
public class Compiler {
    private final ProgramElementCompilerFactory factory;

    public Compiler(ProgramElementCompilerFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
    }

    public Optional<Command> compile(Program program) throws CompilingException {

        var reader = new InputSequenceReader(Preconditions.checkNotNull(program)
                                                          .getCode());

        var optionalProgram = factory.create(LIST_OF_STATEMENTS)
                .compile(reader);

        if (reader.canRead()) {
            throw new CompilingException("Program compilation failed.");
        }

        return optionalProgram;
    }
}
