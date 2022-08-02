package com.teamdev.fsm;

import com.google.common.base.Preconditions;

import java.util.Objects;

/**
 * Represents a certain state of finite state machine and encapsulates a condition of transitioning
 * to it.
 *
 * @param <O> type of output sequence for {@code StateAcceptor}.
 */

public class State<O, E extends Exception> {

    private final String name;

    private final StateAcceptor<O, E> acceptor;
    private final boolean isTemporary;
    private final boolean isFinite;

    private State(Builder<O, E> builder) {
        this.name = builder.name;
        this.acceptor = builder.acceptor;
        this.isTemporary = builder.isTemporary;
        this.isFinite = builder.isFinite;
    }

    public static <O, E extends Exception> State<O, E> initialState() {
        return new Builder<O, E>()
                .setName("INITIAL")
                .setAcceptor(((inputSequence, outputSequence) -> false))
                .build();
    }

    public boolean isTemporary() {
        return isTemporary;
    }

    boolean isFinite() {
        return isFinite;
    }

    StateAcceptor<O, E> getAcceptor() {
        return acceptor;
    }

    @Override
    public String toString() {
        return name;
    }

    public static class Builder<O, E extends Exception> {

        private String name;
        private StateAcceptor<O, E> acceptor;

        private boolean isTemporary = false;

        private boolean isFinite = false;

        public Builder<O, E> setAcceptor(StateAcceptor<O, E> acceptor) {
            Preconditions.checkState(Objects.isNull(this.acceptor), "Acceptor is already set.");
            this.acceptor = Preconditions.checkNotNull(acceptor);

            if (Objects.isNull(name)) {
                name = acceptor.toString();
            }

            return this;
        }

        public Builder<O, E> setTemporary() {
            isTemporary = true;
            return this;
        }

        public Builder<O, E> setFinal() {
            isFinite = true;
            return this;
        }

        public Builder<O, E> setName(String name) {
            this.name = name;
            return this;
        }

        public State<O, E> build() {
            return new State<>(this);
        }
    }
}
