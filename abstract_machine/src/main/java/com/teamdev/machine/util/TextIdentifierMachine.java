package com.teamdev.machine.util;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.fsm.State;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.fsm.TransitionMatrixBuilder;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * {@link FiniteStateMachine} implementation for parsing text from {@link InputSequenceReader}.
 * <p>
 * Requires no whitespaces from the input.
 */

public class TextIdentifierMachine<E extends Exception> extends FiniteStateMachine<StringBuilder, E> {

    private TextIdentifierMachine(TransitionMatrix<StringBuilder, E> transitionMatrix,
                                  ExceptionThrower<E> exceptionThrower) {

        super(transitionMatrix, exceptionThrower, false);
    }

    public static <E extends Exception> Optional<String> parseIdentifier(InputSequenceReader reader,
                                                                         ExceptionThrower<E> exceptionThrower) throws
                                                                                                               E {
        var identifier = new StringBuilder();
        return create(exceptionThrower).accept(reader, identifier) ?
               Optional.of(identifier.toString()) : Optional.empty();

    }

    public static <O, E extends Exception> boolean acceptIdentifier(InputSequenceReader reader,
                                                                    O outputSequence,
                                                                    BiConsumer<O, String> identifierConsumer,
                                                                    ExceptionThrower<E> exceptionThrower) throws
                                                                                                          E {

        var optionalIdentifier = parseIdentifier(reader, exceptionThrower);

        optionalIdentifier.ifPresent(identifier ->
                                             identifierConsumer.accept(
                                                     Preconditions.checkNotNull(outputSequence),
                                                     identifier));

        return optionalIdentifier.isPresent();
    }

    public static <E extends Exception> boolean acceptKeyword(InputSequenceReader reader,
                                                              String keyword,
                                                              ExceptionThrower<E> exceptionThrower) throws
                                                                                                    E {

        Preconditions.checkNotNull(keyword);

        var optionalKeyword = parseIdentifier(reader, exceptionThrower);

        return optionalKeyword.isPresent() && optionalKeyword.get()
                                                             .equals(keyword);
    }

    private static <E extends Exception> TextIdentifierMachine<E> create(
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

        return new TextIdentifierMachine<>(matrix, exceptionThrower);
    }
}
