package com.teamdev.machine.util;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.function.ValidatedFunction;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * {@link FiniteStateMachine} implementation for recognizing names of {@link ValidatedFunction}.
 * <p>
 * Requires no whitespaces from {@link InputSequenceReader}.
 */

public class TextIdentifierMachine<E extends Exception> extends FiniteStateMachine<StringBuilder, E> {

    public static <E extends Exception> Optional<String> parseIdentifier(InputSequenceReader reader,
                                                                         ExceptionThrower<E> exceptionThrower) throws E {
        var identifier = new StringBuilder();
        return create(exceptionThrower).accept(reader, identifier) ?
                Optional.of(identifier.toString()) : Optional.empty();

    }

    public static <O, E extends Exception> boolean acceptIdentifier(InputSequenceReader reader,
                                                                    O outputSequence,
                                                                    BiConsumer<O, String> identifierConsumer,
                                                                    ExceptionThrower<E> exceptionThrower) throws E {

        var optionalIdentifier = parseIdentifier(reader, exceptionThrower);

        optionalIdentifier.ifPresent(identifier ->
                identifierConsumer.accept(Preconditions.checkNotNull(outputSequence), identifier));

        return optionalIdentifier.isPresent();
    }

    public static <E extends Exception> boolean acceptKeyword(InputSequenceReader reader,
                                                              String keyword,
                                                              ExceptionThrower<E> exceptionThrower) throws E {

        Preconditions.checkNotNull(keyword);

        var optionalKeyword = parseIdentifier(reader, exceptionThrower);

        return optionalKeyword.isPresent() && optionalKeyword.get().equals(keyword);
    }

    public static <E extends Exception> TextIdentifierMachine<E> create(
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

        return new TextIdentifierMachine<>(matrix, exceptionThrower, false);
    }

    public boolean acceptName(InputSequenceReader inputSequence, String name) throws E {
        Preconditions.checkNotNull(name);

        var outputSequence = new StringBuilder(16);
        return accept(inputSequence, outputSequence) && outputSequence.toString()
                .equals(name);
    }

    private TextIdentifierMachine(TransitionMatrix<StringBuilder, E> transitionMatrix,
                                  ExceptionThrower<E> exceptionThrower,
                                  boolean skip) {

        super(transitionMatrix, exceptionThrower, skip);
    }
}
