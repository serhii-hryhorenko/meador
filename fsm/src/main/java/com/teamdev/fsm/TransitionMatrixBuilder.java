package com.teamdev.fsm;

import com.google.common.base.Preconditions;

import java.util.*;

public class TransitionMatrixBuilder<O, E extends Exception> {

    private final Map<State<O, E>, Set<State<O, E>>> transitions = new HashMap<>();
    private State<O, E> startState;

    public TransitionMatrixBuilder<O, E> withStartState(State<O, E> state) {
        Preconditions.checkState(startState == null,
                "Start state is already determined.");

        this.startState = Preconditions.checkNotNull(state);
        return this;
    }

    @SafeVarargs
    public final TransitionMatrixBuilder<O, E> allowTransition(State<O, E> state,
                                                               State<O, E>... transitionStates) {
        Preconditions.checkNotNull(state, transitionStates);

        transitions.put(state, new LinkedHashSet<>(Arrays.asList(transitionStates)));
        return this;
    }

    public TransitionMatrix<O, E> build() {
        Preconditions.checkState(Objects.nonNull(startState));

        return new TransitionMatrix<>() {
            @Override
            public State<O, E> getStartState() {
                return startState;
            }

            @Override
            public Set<State<O, E>> getAllowedStates(State<O, E> state) {
                return transitions.get(state);
            }
        };
    }
}
