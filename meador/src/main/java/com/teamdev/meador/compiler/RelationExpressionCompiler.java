package com.teamdev.meador.compiler;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequence;
import com.teamdev.meador.runtime.Command;

import java.util.Optional;

public class RelationExpressionCompiler implements StatementCompiler {

    private final StatementCompilerFactory factory;

    public RelationExpressionCompiler(StatementCompilerFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequence inputSequence) throws CompilingException {
        return Optional.empty();
    }
}
