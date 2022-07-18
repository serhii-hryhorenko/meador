package com.teamdev.meador.compiler.statement.boolean_expr;

import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.runtime.Command;

import java.util.Optional;

public class BooleanExpressionCompiler implements StatementCompiler {

    @Override
    public Optional<Command> compile(InputSequenceReader inputSequence) throws CompilingException {
        return Optional.empty();
    }
}
