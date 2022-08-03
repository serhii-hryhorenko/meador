package com.teamdev.runtime.value.operator.bioperator;

import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.value.type.Value;

@FunctionalInterface
public interface BinaryOperator {
    Value apply(Value left, Value right) throws MeadorRuntimeException;
}
