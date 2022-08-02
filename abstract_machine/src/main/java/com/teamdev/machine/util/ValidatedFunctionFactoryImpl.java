package com.teamdev.machine.util;

import com.teamdev.machine.function.ValidatedFunction;
import com.teamdev.machine.function.ValidatedFunctionFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link ValidatedFunctionFactory} implementation with math functions created with
 * {@link ValidatedFunction.Builder}.
 */
public class ValidatedFunctionFactoryImpl implements ValidatedFunctionFactory {

    private static final int MAX_ARGUMENTS = 10;

    private final Map<String, ValidatedFunction> functions = new HashMap<>();

    public ValidatedFunctionFactoryImpl() {
        functions.put("sum", new ValidatedFunction.Builder()
                .setFunction(doubles -> doubles.stream()
                        .mapToDouble(d -> d)
                        .sum())
                .setMinimumArguments(2)
                .setMaximumArguments(MAX_ARGUMENTS)
                .build()
        );

        functions.put("average", new ValidatedFunction.Builder()
                .setFunction(
                        doubles -> {
                            var sum = doubles.stream()
                                    .mapToDouble(d -> d)
                                    .sum();
                            return sum / doubles.size();
                        })
                .setMinimumArguments(2)
                .setMaximumArguments(MAX_ARGUMENTS)
                .build()
        );

        functions.put("max", new ValidatedFunction.Builder()
                .setFunction(doubles -> doubles.stream().max(Comparator.naturalOrder()).get())
                .setMinimumArguments(2)
                .setMaximumArguments(10)
                .build()
        );

        functions.put("min", new ValidatedFunction.Builder()
                .setFunction(doubles -> doubles.stream().min(Comparator.naturalOrder()).get())
                .setMinimumArguments(2)
                .setMaximumArguments(10)
                .build()
        );

        functions.put("pi", new ValidatedFunction.Builder()
                .setFunction(doubles -> Math.PI)
                .build()
        );

        functions.put("cos", new ValidatedFunction.Builder()
                .setFunction(doubles -> StrictMath.cos(doubles.get(0)))
                .setMinimumArguments(1)
                .setMaximumArguments(1)
                .build()
        );

        functions.put("sin", new ValidatedFunction.Builder()
                .setFunction(doubles -> StrictMath.sin(doubles.get(0)))
                .setMinimumArguments(1)
                .setMaximumArguments(1)
                .build()
        );
    }

    @Override
    public ValidatedFunction create(String name) {
        return functions.get(name);
    }

    @Override
    public boolean hasFunction(String function) {
        return functions.containsKey(function);
    }
}
