package com.teamdev.fsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Provides configuration of transition matrix for {@link FiniteStateMachine} and saves it.
 * {@see https://en.wikipedia.org/wiki/Adjacency_matrix}
 *
 * @param <O>
 */

public interface TransitionMatrix<O, E extends Exception> {

    @SafeVarargs
    static <O, E extends Exception> TransitionMatrix<O, E> chainedTransitions(
            State<O, E>... states) {
        State<O, E> initialState = State.initialState();
        var builder = new TransitionMatrixBuilder<O, E>().withStartState(initialState);

        var chainedStates = new ArrayList<State<O, E>>();
        chainedStates.add(initialState);
        chainedStates.addAll(List.of(states));

        for (var i = 0; i < chainedStates.size() - 1; i++) {
            builder.allowTransition(chainedStates.get(i), chainedStates.get(i + 1));
        }

        return builder.build();
    }

    State<O, E> getStartState();

    Set<State<O, E>> getAllowedStates(State<O, E> state);
}
