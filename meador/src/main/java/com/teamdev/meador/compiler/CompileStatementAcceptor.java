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
    private final StatementCompilerFactory factory;
    private final StatementType type;
    private final BiConsumer<O, Command> resultConsumer;

    public CompileStatementAcceptor(StatementCompilerFactory factory,
                                    StatementType type,
                                    BiConsumer<O, Command> resultConsumer) {
        this.factory = Preconditions.checkNotNull(factory);
        this.type = Preconditions.checkNotNull(type);
        this.resultConsumer = Preconditions.checkNotNull(resultConsumer);
    }

    @Override
    public boolean accept(InputSequenceReader inputSequence, O outputSequence) throws CompilingException {
        var compiler = factory.create(type);

        var optionalCommand = compiler.compile(inputSequence);

        optionalCommand.ifPresent(command -> resultConsumer.accept(outputSequence, command));

        return optionalCommand.isPresent();
    }
}
