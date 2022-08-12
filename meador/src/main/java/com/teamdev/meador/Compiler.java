package com.teamdev.meador;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.programelement.SyntaxException;
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

    public Optional<Command> compile(Program program) throws InvalidProgramException {

        var reader = new InputSequenceReader(Preconditions.checkNotNull(program).getCode());

        try {
            var optionalProgram = factory.create(LIST_OF_STATEMENTS)
                    .compile(reader);

            if (reader.canRead()) {
                throw new SyntaxException("Program compilation stopped unexpectedly.");
            }

            return optionalProgram;
        } catch (SyntaxException ce) {
            var exception = new InvalidProgramException(String.format("%s At index: %d.", ce.getMessage(), reader.getPosition()));
            exception.setErrorPosition(reader.getPosition());
            throw exception;
        }
    }
}
