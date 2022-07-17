package com.teamdev.meador.compiler;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.runtime.Command;

import java.util.Optional;

public class RelationExpressionCompiler implements StatementCompiler {

    private final StatementCompilerFactory factory;

    public RelationExpressionCompiler(StatementCompilerFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader inputSequence) throws CompilingException {
        return Optional.empty();
    }
}
