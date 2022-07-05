package com.teamdev.machine.function;

public interface ValidatedFunctionFactory {

    ValidatedFunction create(String name);

    boolean hasFunction(String function);
}
