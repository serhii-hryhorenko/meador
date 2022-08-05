package com.teamdev.runtime.function;

public interface ValidatedFunctionFactory {

    ValidatedFunction create(String name);

    boolean hasFunction(String function);
}
