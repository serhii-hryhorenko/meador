package com.teamdev.meador.compiler;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.runtime.Command;

import java.util.function.BiConsumer;

/**
 * {@link StateAcceptor} implementation for compiled statements
 *
 * @param <O> custom output chain
 */
public class CompileStatementAcceptor<O> implements StateAcceptor<O, CompilingException> {
    private final ProgramElementCompilerFactory factory;
    private final ProgramElement type;
    private final BiConsumer<O, Command> resultConsumer;

    public CompileStatementAcceptor(ProgramElementCompilerFactory factory,
                                    ProgramElement type,
                                    BiConsumer<O, Command> resultConsumer) {
        this.factory = Preconditions.checkNotNull(factory);
        this.type = Preconditions.checkNotNull(type);
        this.resultConsumer = Preconditions.checkNotNull(resultConsumer);
    }

    @Override
    public boolean accept(InputSequenceReader reader, O outputChain) throws CompilingException {
        var compiler = factory.create(type);

        var optionalCommand = compiler.compile(reader);

        optionalCommand.ifPresent(command -> resultConsumer.accept(outputChain, command));

        return optionalCommand.isPresent();
    }
}
