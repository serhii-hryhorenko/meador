package com.teamdev.machine.util;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.function.ValidatedFunction;

import java.util.Optional;

/**
 * {@link FiniteStateMachine} implementation for recognizing names of {@link ValidatedFunction}.
 * <p>
 * Requires no whitespaces from {@link InputSequenceReader}.
 */

public class TextIdentifierFSM<E extends Exception> extends FiniteStateMachine<StringBuilder, E> {

    public static <E extends Exception> Optional<String> execute(InputSequenceReader inputSequence,
                                                                 ExceptionThrower<E> exceptionThrower) throws E {
        var identifier = new StringBuilder();
        return create(exceptionThrower).accept(inputSequence, identifier) ?
                Optional.of(identifier.toString()) : Optional.empty();
    }

    public static <E extends Exception> TextIdentifierFSM<E> create(
            ExceptionThrower<E> exceptionThrower) {
        Preconditions.checkNotNull(exceptionThrower);

        var initialState = State.<StringBuilder, E>initialState();

        var symbolState = new State.Builder<StringBuilder, E>()
                .setName("SYMBOL")
                .setAcceptor(new SymbolAcceptor<>(Character::isLetter))
                .setFinal()
                .build();

        var matrix = new TransitionMatrixBuilder<StringBuilder, E>()
                .withStartState(initialState)
                .allowTransition(initialState, symbolState)
                .allowTransition(symbolState, symbolState)
                .build();

        return new TextIdentifierFSM<>(matrix, exceptionThrower, false);
    }

    public boolean acceptName(InputSequenceReader inputSequence, String name) throws E {
        Preconditions.checkNotNull(name);

        var outputSequence = new StringBuilder(16);
        return accept(inputSequence, outputSequence) && outputSequence.toString()
                .equals(name);
    }

    private TextIdentifierFSM(TransitionMatrix<StringBuilder, E> transitionMatrix,
                              ExceptionThrower<E> exceptionThrower,
                              boolean skip) {

        super(transitionMatrix, exceptionThrower, skip);
    }
}
