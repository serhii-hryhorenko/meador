package com.teamdev.fsm;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * Accepts transitions from one state to another and operates on the input sequence inside
 * {@link FiniteStateMachine}.
 *
 * @param <O> output sequence type.
 */

@FunctionalInterface
public interface StateAcceptor<O, E extends Exception> {

    static <O, E extends Exception> StateAcceptor<O, E> acceptChar(Character... chars) {
        return (inputSequence, outputSequence) -> {
            if (inputSequence.canRead() && List.of(chars)
                    .contains(inputSequence.read())) {
                inputSequence.next();
                return true;
            }

            return false;
        };
    }

    boolean accept(InputSequenceReader inputSequence, O outputSequence) throws E;

    default StateAcceptor<O, E> named(String name) {
        var old = this;

        return new StateAcceptor<>() {
            @Override
            public boolean accept(InputSequenceReader inputSequence, O outputSequence) throws E {
                return old.accept(inputSequence, outputSequence);
            }

            @Override
            public String toString() {
                return Preconditions.checkNotNull(name);
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
