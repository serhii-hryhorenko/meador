package com.teamdev.meador.fsmimpl.util;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.compiler.StatementType;
import com.teamdev.runtime.Command;

import java.util.function.BiConsumer;

/**
 * {@link FiniteStateMachine} implementation for recognizing Meador Statements inside curly braces.
 *
 * @param <O> output chain
 */
public class CodeBlockFSM<O> extends FiniteStateMachine<O, CompilingException> {
    public static <O> CodeBlockFSM<O> create(StatementCompilerFactory factory,
                                             BiConsumer<O, Command> blockConsumer) {
        Preconditions.checkNotNull(factory);

        var openCurlyBracket = new State.Builder<O, CompilingException>()
                .setName("CODE BLOCK START")
                .setAcceptor(StateAcceptor.acceptChar('{'))
                .build();

        var program = new State.Builder<O, CompilingException>()
                .setName("CODE BLOCK")
                .setAcceptor(new CompileStatementAcceptor<>(factory, StatementType.PROGRAM, blockConsumer))
                .build();

        var closeCurlyBracket = new State.Builder<O, CompilingException>()
                .setName("CODE BLOCK END")
                .setAcceptor(StateAcceptor.acceptChar('}'))
                .setFinite(true)
                .build();

        var matrix = TransitionMatrix.chainedTransitions(openCurlyBracket, program, closeCurlyBracket);
        return new CodeBlockFSM<>(matrix, new ExceptionThrower<>(CompilingException::new));
    }

    private CodeBlockFSM(TransitionMatrix<O, CompilingException> transitionMatrix,
                         ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
