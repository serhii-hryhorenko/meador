package com.teamdev.meador.programelement.variable;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.programelement.CompilingException;
import com.teamdev.meador.programelement.ProgramElementCompiler;
import com.teamdev.runtime.Command;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for providing access to variable values stored at
 * {@link com.teamdev.runtime.Memory}.
 */
public class VariableValueCompiler implements ProgramElementCompiler {

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        var optionalName = TextIdentifierMachine.parseIdentifier(reader,
                                                                 new ExceptionThrower<>(
                                                                         CompilingException::new));

        return optionalName.map(variableName -> runtimeEnvironment -> {

            var value = runtimeEnvironment.memory()
                                          .getVariable(variableName);

            runtimeEnvironment.stack()
                              .peek()
                              .pushOperand(value);
        });

    }

    public Optional<Command> compile(String variableName) {
        return Optional.of(runtimeEnvironment -> {

            var variable = runtimeEnvironment.memory()
                                             .getVariable(Preconditions.checkNotNull(variableName));

            runtimeEnvironment.stack()
                              .peek()
                              .pushOperand(variable);
        });
    }
}
