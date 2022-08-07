package com.teamdev.runtime;

import com.teamdev.runtime.evaluation.TypeMismatchException;
import com.teamdev.runtime.evaluation.operandtype.BooleanValueVisitor;

/**
 * Runtime command which is created in compile time and evaluated on a runtime.
 */
@FunctionalInterface
public interface Command {

    void execute(RuntimeEnvironment environment) throws MeadorRuntimeException;

    default boolean checkCondition(RuntimeEnvironment environment) throws MeadorRuntimeException {
        environment.stack()
                .create();

        execute(environment);

        var condition = environment.stack()
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
