package com.teamdev.fsm;

import com.google.common.base.Preconditions;

public class TransitionOneOfMatrixBuilder<O, E extends Exception> {

    private final State<O, E> initial = State.initialState();
    private final TransitionMatrixBuilder<O, E> builder = new TransitionMatrixBuilder<>();

    public final TransitionOneOfMatrixBuilder<O, E> allowTransition(StateAcceptor<O, E> acceptor) {
        Preconditions.checkNotNull(acceptor);

        var state = new State.Builder<O, E>()
                .setAcceptor(acceptor)
                .setFinal()
                .build();

        builder.allowTransition(initial, state);
        return this;
    }

    public final TransitionOneOfMatrixBuilder<O, E> allowTransition(StateAcceptor<O, E> acceptor, boolean isTemporary) {
        Preconditions.checkNotNull(acceptor);

        var state = new State.Builder<O, E>()
                .setAcceptor(acceptor)
                .setFinal();

        if (isTemporary) {
            state.setTemporary();
        }

        builder.allowTransition(initial, state.build());
        return this;
    }

    public final TransitionMatrix<O, E> build() {
        return builder.withStartState(initial).build();
    }
}
