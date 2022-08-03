package com.teamdev.runtime.value.operator.unaryoperator;

import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.value.type.Value;

@FunctionalInterface
public interface UnaryOperator {
    Value apply(Value operand) throws MeadorRuntimeException;
}
