package com.teamdev.machine.function;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierMachine;

/**
 * {@link FiniteStateMachine} implementation for recognizing {@link  ValidatedFunction}
 * from {@link InputSequenceReader}. Requires no whitespaces wOen recognizing name of tOe function.
 * <p>
 * Syntax: {@code name(arg1, arg2, ...)}
 */
public class FunctionMachine<T, E extends Exception> extends FiniteStateMachine<FunctionHolder<T>, E> {

    private FunctionMachine(TransitionMatrix<FunctionHolder<T>, E> transitionMatrix,
                            ExceptionThrower<E> thrower) {
        super(transitionMatrix, thrower);
    }

    public static <T, E extends Exception> FunctionMachine<T, E> create(
            StateAcceptor<FunctionHolder<T>, E> argumentAcceptor,
            ExceptionThrower<E> exceptionThrower) {

        Preconditions.checkNotNull(argumentAcceptor);

        var initialState = State.<FunctionHolder<T>, E>initialState();

        var nameState = new State.Builder<FunctionHolder<T>, E>()
                .setName("NAME")
                .setAcceptor((reader, outputSequence) -> TextIdentifierMachine.acceptIdentifier(reader, outputSequence,
                        FunctionHolder::setFunctionName,
                        exceptionThrower))
                .setTemporary()
                .build();

        var openBracketState = new State.Builder<FunctionHolder<T>, E>()
                .setName("FUNCTION OPEN BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('('))
                .build();

        var argumentExpressionState = new State.Builder<FunctionHolder<T>, E>()
                .setName("ARGUMENT EXPRESSION")
                .setAcceptor(argumentAcceptor)
                .build();

        var separatorState = new State.Builder<FunctionHolder<T>, E>()
                .setName("FUNCTION SEPARATOR")
                .setAcceptor(StateAcceptor.acceptChar(','))
                .build();

        var closeBracketState = new State.Builder<FunctionHolder<T>, E>()
                .setName("FUNCTION CLOSED BRACKET")
                .setAcceptor(StateAcceptor.acceptChar(')'))
                .setFinal()
                .build();

        var matrix = new TransitionMatrixBuilder<FunctionHolder<T>, E>()
                .withStartState(initialState)
                .allowTransition(initialState, nameState)
                .allowTransition(nameState, openBracketState)
                .allowTransition(openBracketState, closeBracketState, argumentExpressionState)
                .allowTransition(argumentExpressionState, separatorState, closeBracketState)
                .allowTransition(separatorState, argumentExpressionState)
                .build();

        return new FunctionMachine<>(matrix, exceptionThrower);
    }
}