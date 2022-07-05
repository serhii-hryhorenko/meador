package com.teamdev.meador.compiler.statement.procedure;

import com.google.common.base.Preconditions;
import com.teamdev.math.type.Value;
import com.teamdev.meador.runtime.RuntimeEnvironment;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class ValidatedProcedure implements BiConsumer<List<Value>, RuntimeEnvironment> {

    private final BiConsumer<List<Value>, RuntimeEnvironment> action;
    private final int minArguments;
    private final int maxArguments;

    private ValidatedProcedure(
            BiConsumer<List<Value>, RuntimeEnvironment> action, int minArguments,
            int maxArguments) {
        this.action = Preconditions.checkNotNull(action);
        this.minArguments = minArguments;
        this.maxArguments = maxArguments;
    }

    @Override
    public void accept(List<Value> arguments, RuntimeEnvironment environment) {
        action.accept(arguments, environment);
    }

    int minArguments() {
        return minArguments;
    }

    int maxArguments() {
        return maxArguments;
    }

    public static class Builder {
        private BiConsumer<List<Value>, RuntimeEnvironment> action;
        private int minArguments = 0;
        private int maxArguments = 0;

        public Builder setAction(
                BiConsumer<List<Value>, RuntimeEnvironment> action) {
            this.action = Preconditions.checkNotNull(action);
            return this;
        }

        public Builder setMinArguments(int minArguments) {
            this.minArguments = minArguments;
            return this;
        }

        public Builder setMaxArguments(int maxArguments) {
            this.maxArguments = maxArguments;
            return this;
        }

        public ValidatedProcedure build() {
            Preconditions.checkState(Objects.nonNull(action));
            return new ValidatedProcedure(action, minArguments, maxArguments);
        }
    }
}
