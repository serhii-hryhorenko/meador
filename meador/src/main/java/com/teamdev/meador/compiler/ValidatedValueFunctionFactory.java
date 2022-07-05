package com.teamdev.meador.compiler;

import com.teamdev.machine.function.ValidatedFunction;
import com.teamdev.machine.function.ValidatedFunctionFactory;

import java.util.HashMap;
import java.util.Map;

public class ValidatedValueFunctionFactory implements ValidatedFunctionFactory {

    private static final int MAX_ARGUMENTS = 10;

    private final Map<String, ValidatedFunction> functions = new HashMap<>();

    ValidatedValueFunctionFactory() {
        functions.put("sum", new ValidatedFunction.Builder()
                .setFunction(doubles -> doubles.stream()
                                       .mapToDouble(value -> value)
                                       .sum())
                .setMinimumArguments(2)
                .setMaximumArguments(MAX_ARGUMENTS)
                .build()
        );

        functions.put("average", new ValidatedFunction.Builder()
                .setFunction(doubles -> {
                    var sum = doubles.stream()
                                        .mapToDouble(value -> value)
                                        .sum();

                    return sum / doubles.size();
                })
                .setMinimumArguments(2)
                .setMaximumArguments(MAX_ARGUMENTS)
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