package com.teamdev.machine.brackets;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;

/**
 * {@link FiniteStateMachine} implementation that recognizes expressions
 * surrounded with math round brackets and provides the right order of evaluation.
 */
public class BracketsMachine<O, E extends Exception> extends FiniteStateMachine<O, E> {

    private BracketsMachine(TransitionMatrix<O, E> transitionMatrix,
                            ExceptionThrower<E> thrower) {
        super(transitionMatrix, thrower);
    }

    public static <O, E extends Exception> BracketsMachine<O, E> create(
            StateAcceptor<O, E> expression,
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

        return new BracketsMachine<>(matrix, thrower);
    }
}
