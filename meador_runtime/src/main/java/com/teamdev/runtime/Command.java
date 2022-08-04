package com.teamdev.runtime;

import com.teamdev.runtime.value.type.Value;
import com.teamdev.runtime.value.type.bool.BooleanValueVisitor;

/**
 * Runtime command which is created in compile time and evaluated on a runtime.
 */
@FunctionalInterface
public interface Command {

    void execute(RuntimeEnvironment runtimeEnvironment) throws MeadorRuntimeException;

    default boolean checkCondition(RuntimeEnvironment runtimeEnvironment, Command booleanExpression) throws MeadorRuntimeException {
        runtimeEnvironment.stack().create();
        booleanExpression.execute(runtimeEnvironment);
        Value condition = runtimeEnvironment.stack().pop().popResult();

        var visitor = new BooleanValueVisitor();
        condition.acceptVisitor(visitor);

        return visitor.value();
    }
}
