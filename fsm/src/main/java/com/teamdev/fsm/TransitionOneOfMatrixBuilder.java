package com.teamdev.fsm;

import com.google.common.base.Preconditions;

import java.util.LinkedHashSet;
import java.util.Set;

public class TransitionOneOfMatrixBuilder<O, E extends Exception> {

    private final State<O, E> initial = State.initialState();

    private final Set<State<O, E>> transitions = new LinkedHashSet<>();

    private final TransitionMatrixBuilder<O, E> builder = new TransitionMatrixBuilder<>();

    public final TransitionOneOfMatrixBuilder<O, E> allowTransition(StateAcceptor<O, E> acceptor,
                                                                    String stateName) {
        Preconditions.checkNotNull(acceptor, stateName);

        var state = new State.Builder<O, E>()
                .setName(stateName)
                .setAcceptor(acceptor)
                .setFinite(true)
                .build();

        transitions.add(state);
        return this;
    }

    public final TransitionOneOfMatrixBuilder<O, E> allowTransition(StateAcceptor<O, E> acceptor,
                                                                    String stateName,
                                                                    boolean isTemporary) {
        Preconditions.checkNotNull(acceptor, stateName);

        var state = new State.Builder<O, E>()
                .setName(stateName)
                .setAcceptor(acceptor)
                .setFinite(true)
                .setTemporary(isTemporary)
                .build();

        transitions.add(state);
        return this;
    }

    public final TransitionMatrix<O, E> build() {
        return builder.withStartState(initial)
                      .allowTransition(initial, transitions.toArray(new State[transitions.size()]))
                      .build();
    }
}
