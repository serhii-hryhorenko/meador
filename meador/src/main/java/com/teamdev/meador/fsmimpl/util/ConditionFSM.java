package com.teamdev.meador.fsmimpl.util;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.compiler.StatementType;
import com.teamdev.runtime.Command;

import java.util.function.BiConsumer;

public class ConditionFSM<O> extends FiniteStateMachine<O, CompilingException> {

    public static <O> ConditionFSM<O> create(StatementCompilerFactory factory,
                                             StatementType type,
                                             BiConsumer<O, Command> resultConsumer) {
        Preconditions.checkNotNull(factory, type);

        var openBracket = new State.Builder<O, CompilingException>()
                .setName("OPEN BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('('))
                .build();

        var expressionToMatch = new State.Builder<O, CompilingException>()
                .setName("EXPRESSION TO MATCH")
                .setAcceptor(new CompileStatementAcceptor<>(factory, type, Preconditions.checkNotNull(resultConsumer)))
                .build();

        var closeBracket = new State.Builder<O, CompilingException>()
                .setName("CLOSE BRACKET")
                .setAcceptor(StateAcceptor.acceptChar(')'))
                .setFinite(true)
                .build();

        var matrix = TransitionMatrix.chainedTransitions(openBracket, expressionToMatch, closeBracket);

        return new ConditionFSM<>(matrix, new ExceptionThrower<>(CompilingException::new));
    }

    private ConditionFSM(TransitionMatrix<O, CompilingException> transitionMatrix,
                         ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
