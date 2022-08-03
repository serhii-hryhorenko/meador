package com.teamdev.machine.number;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.SymbolAcceptor;
import com.teamdev.runtime.value.type.number.NumericValue;
import com.teamdev.runtime.value.type.Value;

import java.util.Optional;

/**
 * {@link  FiniteStateMachine} implementation for recognizing integer and float numbers.
 * Requires no whitespaces from {@link InputSequenceReader}.
 */

public class NumberMachine<E extends Exception> extends FiniteStateMachine<StringBuilder, E> {

    private NumberMachine(TransitionMatrix<StringBuilder, E> transitionMatrix,
                          ExceptionThrower<E> exceptionThrower) {
        super(transitionMatrix, exceptionThrower, false);
    }

    public static <E extends Exception> Optional<Value> execute(InputSequenceReader inputSequence,
                                                                ExceptionThrower<E> exceptionThrower) throws
            E {
        var number = create(Preconditions.checkNotNull(exceptionThrower));
        var outputSequence = new StringBuilder();

        if (number.accept(inputSequence, outputSequence)) {
            return Optional.of(new NumericValue(Double.parseDouble(outputSequence.toString())));
        }

        return Optional.empty();
    }

    public static <E extends Exception> NumberMachine<E> create(ExceptionThrower<E> exceptionThrower) {
        State<StringBuilder, E> initialState = State.initialState();

        var signState = new State.Builder<StringBuilder, E>()
                .setName("SIGN")
                .setAcceptor(new SymbolAcceptor<>('+', '-'))
                .build();

        var integerPartState = new State.Builder<StringBuilder, E>()
                .setName("INTEGER PART")
                .setAcceptor(new SymbolAcceptor<>(Character::isDigit))
                .setFinal()
                .build();

        var dotState = new State.Builder<StringBuilder, E>()
                .setName("DOT")
                .setAcceptor(new SymbolAcceptor<>('.'))
                .build();

        var floatPartState = new State.Builder<StringBuilder, E>()
                .setName("FLOAT PART")
                .setAcceptor(new SymbolAcceptor<>(Character::isDigit))
                .setFinal()
                .build();

        var matrix = new TransitionMatrixBuilder<StringBuilder, E>()
                .withStartState(initialState)
                .allowTransition(initialState, signState, integerPartState)
                .allowTransition(signState, integerPartState)
                .allowTransition(integerPartState, integerPartState, dotState)
                .allowTransition(dotState, floatPartState)
                .allowTransition(floatPartState, floatPartState)
                .build();

        return new NumberMachine<>(matrix, Preconditions.checkNotNull(exceptionThrower));
    }
}
