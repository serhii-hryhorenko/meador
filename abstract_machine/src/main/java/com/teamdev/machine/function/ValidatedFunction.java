package com.teamdev.machine.function;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.runtime.value.type.number.NumericValue;
import com.teamdev.runtime.value.type.number.NumericValueVisitor;
import com.teamdev.runtime.value.type.Value;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Internal representation of math functions.
 * Implementation has {@code minimalArguments}, {@code minimalArguments} fields which
 * determine requested number of arguments from {@link InputSequenceReader}.
 */
public class ValidatedFunction implements Function<List<Value>, Value> {

    private final Function<List<Double>, Double> function;
    private final int minArguments;
    private final int maxArguments;

    private ValidatedFunction(Builder builder) {
        this.function = builder.function;
        this.minArguments = builder.minArguments;
        this.maxArguments = builder.maxArguments;
    }

    public int getMinArguments() {
        return minArguments;
    }

    public int getMaxArguments() {
        return maxArguments;
    }

    @Override
    public Value apply(List<Value> values) {
        var doubles = values.stream()
                .mapToDouble(value -> {
                    var visitor = new NumericValueVisitor();
                    value.acceptVisitor(visitor);
                    return visitor.value();
                })
                .boxed().toList();

        return new NumericValue(function.apply(doubles));
    }

    public static class Builder {
        private Function<List<Double>, Double> function;
        private int minArguments = 0;
        private int maxArguments = 0;

        public Builder setFunction(Function<List<Double>, Double> function) {
            Preconditions.checkState(this.function == null);

            this.function = Preconditions.checkNotNull(function);
            return this;
        }

        public Builder setMaximumArguments(int max) {
            Preconditions.checkArgument(max >= 0);

            this.maxArguments = max;
            return this;
        }

        public Builder setMinimumArguments(int min) {
            Preconditions.checkArgument(min >= 0);

            this.minArguments = min;
            return this;
        }

        public ValidatedFunction build() {

            Preconditions.checkState(maxArguments >= minArguments,
                    "Minimal number of arguments can't be greater than maximum.");

            return new ValidatedFunction(this);
        }
    }
}
