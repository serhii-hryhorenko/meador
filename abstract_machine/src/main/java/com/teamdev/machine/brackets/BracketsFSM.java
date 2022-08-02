package com.teamdev.machine.brackets;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;

/**
 * {@link FiniteStateMachine} implementation that recognizes expressions
 * surrounded with math round brackets and provides the right order of evaluation.
 */
public class BracketsFSM<O, E extends Exception> extends FiniteStateMachine<O, E> {

    private BracketsFSM(TransitionMatrix<O, E> transitionMatrix,
                        ExceptionThrower<E> thrower) {
        super(transitionMatrix, thrower);
    }

    public static <O, E extends Exception> BracketsFSM<O, E> create(StateAcceptor<O, E> expression,
                                                                    ExceptionThrower<E> thrower) {
        Preconditions.checkNotNull(expression);

        var openBracketState = new State.Builder<O, E>()
                .setName("OPEN BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('('))
                .build();

        var expressionState = new State.Builder<O, E>()
                .setName("NESTED EXPRESSION")
                .setAcceptor(expression)
                .build();

        var closedBracketState = new State.Builder<O, E>()
                .setName("CLOSED BRACKET")
                .setAcceptor(StateAcceptor.acceptChar(')'))
                .setFinal()
                .build();

        var matrix = TransitionMatrix.chainedTransitions(
                openBracketState, expressionState, closedBracketState
        );

        return new BracketsFSM<>(matrix, thrower);
    }
}
