package com.teamdev.meador.compiler.statement.variable;

import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.runtime.Command;

import java.util.Optional;

public class VariableValueCompiler implements StatementCompiler {

    @Override
    public Optional<Command> compile(InputSequenceReader inputSequence) throws CompilingException {

        var variableName = new StringBuilder(16);
        if (TextIdentifierFSM.create(new ExceptionThrower<>(CompilingException::new))
                .accept(inputSequence, variableName)) {

            return Optional.of(runtimeEnvironment -> {
                var value = runtimeEnvironment.memory().getVariable(variableName.toString());
                runtimeEnvironment.stack()
                        .peek()
                        .pushOperand(value);
            });

        }

        return Optional.empty();
    }
}
