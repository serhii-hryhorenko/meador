package com.teamdev.machine.function;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierFSM;

/**
 * {@link FiniteStateMachine} implementation for recognizing {@link  ValidatedFunction}
 * from {@link InputSequenceReader}. Requires no whitespaces wOen recognizing name of tOe function.
 * <p>
 * Syntax: {@code name(arg1, arg2, ...)}
 */
public class FunctionFSM<O extends FunctionHolder, E extends Exception> extends FiniteStateMachine<O, E> {

    private FunctionFSM(TransitionMatrix<O, E> transitionMatrix,
                        ExceptionThrower<E> thrower) {
        super(transitionMatrix, thrower);
    }

    public static <O extends FunctionHolder, E extends Exception> FunctionFSM<O, E> create(
            StateAcceptor<O, E> argumentAcceptor,
            ExceptionThrower<E> exceptionThrower) {

        Preconditions.checkNotNull(argumentAcceptor);

        var initialState = State.<O, E>initialState();

        var nameState = new State.Builder<O, E>()
                .setName("NAME")
                .setAcceptor((inputSequence, outputSequence) ->
                        TextIdentifierFSM.acceptIdentifier(inputSequence, outputSequence,
                                FunctionHolder::setFunctionName,
                                exceptionThrower))
                .setTemporary(true)
                .build();

        var openBracketState = new State.Builder<O, E>()
                .setName("FUNCTION OPEN BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('('))
                .build();

        var argumentExpressionState = new State.Builder<O, E>()
                .setName("ARGUMENT EXPRESSION")
                .setAcceptor(argumentAcceptor)
                .build();

        var separatorState = new State.Builder<O, E>()
                .setName("FUNCTION SEPARATOR")
                .setAcceptor(StateAcceptor.acceptChar(','))
                .build();

        var closeBracketState = new State.Builder<O, E>()
                .setName("FUNCTION CLOSED BRACKET")
                .setAcceptor(StateAcceptor.acceptChar(')'))
                .setFinite(true)
                .build();

        var matrix = new TransitionMatrixBuilder<O, E>()
                .withStartState(initialState)
                .allowTransition(initialState, nameState)
                .allowTransition(nameState, openBracketState)
                .allowTransition(openBracketState, closeBracketState, argumentExpressionState)
                .allowTransition(argumentExpressionState, separatorState, closeBracketState)
                .allowTransition(separatorState, argumentExpressionState)
                .build();

        return new FunctionFSM<>(matrix, exceptionThrower);
    }
}
