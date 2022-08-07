package com.teamdev.runtime;

import com.teamdev.runtime.evaluation.TypeMismatchException;
import com.teamdev.runtime.evaluation.operandtype.BooleanValueVisitor;
import com.teamdev.runtime.evaluation.operandtype.Value;

/**
 * Runtime command which is created in compile time and evaluated on a runtime.
 */
@FunctionalInterface
public interface Command {

    void execute(RuntimeEnvironment runtimeEnvironment) throws MeadorRuntimeException;

    default boolean checkCondition(RuntimeEnvironment runtimeEnvironment) throws MeadorRuntimeException {
        runtimeEnvironment.stack()
                .create();

        execute(runtimeEnvironment);

        var condition = runtimeEnvironment.stack()
                                            .pop()
                                            .popResult();

        var visitor = new BooleanValueVisitor();

        try {
            condition.acceptVisitor(visitor);
        } catch (TypeMismatchException e) {
            throw new MeadorRuntimeException(e.getMessage());
        }

        return visitor.value();
    }
}
