package com.teamdev.fsm;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Realization of finite state machine concept based on {@link StateAcceptor} for accepting
 * transitions between {@link State}.
 * {@linktourl https://en.wikipedia.org/wiki/Finite-state_machine}
 *
 * <p>Requires {@link TransitionMatrix} as a definition of assigned directed graph.
 * Realizes traversal algorithm from a so-called start state to so-called finish state
 * that are defined by a transition matrix also.
 *
 * @param <O> output sequence type
 */

public class FiniteStateMachine<O, E extends Exception> implements StateAcceptor<O, E> {

    private static final Logger logger = LoggerFactory.getLogger(FiniteStateMachine.class);

    private final TransitionMatrix<O, E> transitionMatrix;
    private boolean skippingWhitespaces = true;

    private final ExceptionThrower<E> exceptionThrower;

    protected FiniteStateMachine(TransitionMatrix<O, E> transitionMatrix, ExceptionThrower<E> thrower) {

        this.transitionMatrix = Preconditions.checkNotNull(transitionMatrix);
        this.exceptionThrower = Preconditions.checkNotNull(thrower);
    }

    protected FiniteStateMachine(TransitionMatrix<O, E> transitionMatrix, ExceptionThrower<E> thrower, boolean skippingWhitespaces) {

        this.transitionMatrix = Preconditions.checkNotNull(transitionMatrix);
        this.exceptionThrower = Preconditions.checkNotNull(thrower);
        this.skippingWhitespaces = skippingWhitespaces;
    }

    public static <O, E extends Exception> FiniteStateMachine<O, E> oneOf(String name,
                                                                          TransitionOneOfMatrixBuilder<O, E> matrixBuilder,
                                                                          ExceptionThrower<E> exceptionThrower) {

        Preconditions.checkNotNull(matrixBuilder, exceptionThrower);
        Preconditions.checkNotNull(name);

        return new FiniteStateMachine<>(matrixBuilder.build(), exceptionThrower) {
            @Override
            public String toString() {
                return name;
            }
        };
    }

    @Override
    public boolean accept(InputSequenceReader input, O output) throws E {
        Preconditions.checkNotNull(input, output);

        if (logger.isInfoEnabled()) {
            logger.info("[{}] runs with [{}] input sequence", this, input.getSequence());
        }

        var currentState = transitionMatrix.getStartState();

        while (true) {
            if (skippingWhitespaces) {
                input.skipWhitespaces();
            }

            var nextState = getNextState(currentState, input, output);

            if (nextState.isEmpty()) {
                if (transitionMatrix.getStartState().equals(currentState)) {
                    if (logger.isInfoEnabled()) {
                        logger.info("[{}] is failed to start.", this);
                    }

                    return false;
                }


                if (currentState.isFinite()) {

                    if (logger.isInfoEnabled()) {
                        logger.info("[{}]: finished successfully in [{}]", this, currentState);
                    }

                    return true;
                }

                if (currentState.isTemporary()) {
                    input.restorePosition();
                    return false;
                }

                if (logger.isErrorEnabled()) {
                    logger.error("[{}] got into deadlock when [{}]", this, currentState);
                }

                exceptionThrower.throwException();
            }

            currentState = nextState.get();
        }
    }

    private void restore(InputSequenceReader input, State<O, E> state) {
        if (state.isTemporary()) {
            input.restorePosition();

            if (logger.isInfoEnabled()) {
                logger.info("[{}] restored to [{}], index: {}.", this, input.getSequence(), input.getPosition());
            }
        }
    }

    private Optional<State<O, E>> getNextState(State<O, E> currentState, InputSequenceReader input, O output) throws E {

        var allowedStates = Optional.ofNullable(transitionMatrix.getAllowedStates(currentState));

        if (allowedStates.isPresent()) {
            for (var candidateState : allowedStates.get()) {

                if (candidateState.isTemporary()) {
                    input.savePosition();

                    if (logger.isInfoEnabled()) {
                        logger.info("[{}] saved InputSequence at position [{}], index: {}.", this, input.getSequence(), input.getPosition());
                    }
                }

                if (candidateState.getAcceptor().accept(input, output)) {

                    if (logger.isInfoEnabled()) {
                        logger.info("[{}]: [{}] -> [{}]", this, currentState, candidateState);
                    }

                    return Optional.of(candidateState);
                }
                restore(input, candidateState);
            }
        }

        return Optional.empty();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
