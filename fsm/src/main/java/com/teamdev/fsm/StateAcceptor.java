package com.teamdev.fsm;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Supplier;

/**
 * Accepts transitions from one state to another and operates on the input sequence inside
 * {@link FiniteStateMachine}.
 *
 * @param <O>
 *         output sequence type.
 */

@FunctionalInterface
public interface StateAcceptor<O, E extends Exception> {

    Logger logger = LoggerFactory.getLogger(StateAcceptor.class);

    static <O, E extends Exception> StateAcceptor<O, E> acceptChar(Character... chars) {
        return (inputSequence, outputSequence) -> {
            if (inputSequence.canRead() && List.of(chars)
                                               .contains(inputSequence.read())) {
                if (logger.isInfoEnabled()) {
                    logger.info("Parsed `{}` symbol.", inputSequence.read());
                }

                inputSequence.next();
                return true;
            }

            return false;
        };
    }

    boolean accept(InputSequenceReader reader, O outputChain) throws E;

    default int parseInDepth(InputSequenceReader inputSequence, Supplier<O> outputChainSupplier) {
        var output = outputChainSupplier.get();
        final int startPosition = inputSequence.getPosition();

        inputSequence.savePosition();
        var readerState = inputSequence.dumpState();

        if (logger.isInfoEnabled()) {
            logger.info("[{}] started parsing in depth. Reader sequence: {}, index: {}.",
                        this, inputSequence.getSequence(), inputSequence.getPosition());
        }

        try {
            accept(inputSequence, output);
        } catch (Exception deadlock) {
            if (logger.isInfoEnabled()) {
                logger.info("[{}] got into deadlock while parsing. Reader sequence: {}, index: {}.",
                            this, inputSequence.getSequence(), inputSequence.getPosition());
            }

            inputSequence.setState(readerState);
            inputSequence.restorePosition();
            return -1;
        }

        final int parsed = inputSequence.getPosition();
        inputSequence.setState(readerState);
        inputSequence.restorePosition();

        int depth = parsed - startPosition;

        if (logger.isInfoEnabled()) {
            logger.info("Reader position is restored to {}, index: {}.",
                        inputSequence.getSequence(), inputSequence.getPosition());

            logger.info("[{}] parsed {} symbols.",
                        this, depth);
        }

        return depth;
    }

    default StateAcceptor<O, E> named(String name) {
        Preconditions.checkNotNull(name);
        var old = this;

        return new StateAcceptor<>() {
            @Override
            public boolean accept(InputSequenceReader reader, O outputChain) throws E {
                return old.accept(reader, outputChain);
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }

    default StateAcceptor<O, E> and(StateAcceptor<O, E> other) {
        return (inputSequence, outputSequence) ->
                this.accept(inputSequence, outputSequence) &&
                        Preconditions.checkNotNull(other)
                                     .accept(inputSequence, outputSequence);
    }
}
