package com.teamdev.machine.number;

import com.google.common.base.Preconditions;
import com.teamdev.machine.util.SymbolAcceptor;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.InputSequence;
import com.teamdev.fsm.State;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.fsm.TransitionMatrixBuilder;
import com.teamdev.math.type.DoubleValue;
import com.teamdev.math.type.Value;

import java.util.Optional;

/**
 * {@link  FiniteStateMachine} implementation for recognizing integer and float numbers.
 * Requires no whitespaces from {@link InputSequence}.
 */

public class NumberFSM<E extends Exception> extends FiniteStateMachine<StringBuilder, E> {

    private NumberFSM(TransitionMatrix<StringBuilder, E> transitionMatrix,
                      ExceptionThrower<E> exceptionThrower,
                      boolean skip) {
        super(transitionMatrix, exceptionThrower, skip);
    }

    public static <E extends Exception> Optional<Value> execute(InputSequence inputSequence,
                                                                ExceptionThrower<E> exceptionThrower) throws
                                                                                                       E {
        var number = create(Preconditions.checkNotNull(exceptionThrower));
        var outputSequence = new StringBuilder();

        if (number.accept(inputSequence, outputSequence)) {

            return Optional.of(new DoubleValue(Double.parseDouble(outputSequence.toString())));
        }

        return Optional.empty();
    }

    public static <E extends Exception> NumberFSM<E> create(ExceptionThrower<E> exceptionThrower) {
        State<StringBuilder, E> initialState = State.initialState();

        var signState = new State.Builder<StringBuilder, E>()
                .setName("SIGN")
                .setAcceptor(new SymbolAcceptor<>('+', '-'))
                .build();

        var integerPartState = new State.Builder<StringBuilder, E>()
                .setName("INTEGER PART")
                .setAcceptor(new SymbolAcceptor<>(Character::isDigit))
                .setFinite(true)
                .build();

        var dotState = new State.Builder<StringBuilder, E>()
                .setName("DOT")
                .setAcceptor(new SymbolAcceptor<>('.'))
                .build();

        var floatPartState = new State.Builder<StringBuilder, E>()
                .setName("FLOAT PART")
                .setAcceptor(new SymbolAcceptor<>(Character::isDigit))
                .setFinite(true)
                .build();

        var matrix = new TransitionMatrixBuilder<StringBuilder, E>()
                .withStartState(initialState)
                .allowTransition(initialState, signState, integerPartState)
                .allowTransition(signState, integerPartState)
                .allowTransition(integerPartState, integerPartState, dotState)
                .allowTransition(dotState, floatPartState)
                .allowTransition(floatPartState, floatPartState)
                .build();

        return new NumberFSM<>(matrix, Preconditions.checkNotNull(exceptionThrower), false);
    }
}
