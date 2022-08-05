package com.teamdev.runtime.function;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.evaluation.TypeMismatchException;
import com.teamdev.runtime.evaluation.operandtype.NumericValue;
import com.teamdev.runtime.evaluation.operandtype.NumericValueVisitor;
import com.teamdev.runtime.evaluation.operandtype.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Internal representation of math functions.
 * Implementation has {@code minimalArguments}, {@code minimalArguments} fields which
 * determine requested number of arguments from program.
 */
public class ValidatedFunction {

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

    public Value apply(Iterable<Value> arguments) throws MeadorRuntimeException {
        var visitor = new NumericValueVisitor();
        List<Double> doubles = new ArrayList<>();

        for (var argument : arguments) {
            try {
                argument.acceptVisitor(visitor);
            } catch (TypeMismatchException e) {
                throw new MeadorRuntimeException(e.getMessage());
            }

            doubles.add(visitor.value());
        }

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
