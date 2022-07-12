package com.teamdev.meador.compiler.statement.variable;

import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.InputSequence;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.meador.runtime.Command;

import java.util.NoSuchElementException;
import java.util.Optional;

public class VariableValueCompiler implements StatementCompiler {

    @Override
    public Optional<Command> compile(InputSequence inputSequence) throws CompilingException {

        var variableName = new StringBuilder(16);
        if (TextIdentifierFSM.create(new ExceptionThrower<>(CompilingException::new))
                .accept(inputSequence, variableName)) {

            return Optional.of(runtimeEnvironment -> {
                var optionalValue = runtimeEnvironment.memory().getVariable(variableName.toString());

                optionalValue.ifPresentOrElse(
                        value -> runtimeEnvironment.stack()
                                .peek()
                                .pushOperand(value),
                        () -> {
                            throw new NoSuchElementException();
                        }
                );
            });
        }

        return Optional.empty();
    }
}
