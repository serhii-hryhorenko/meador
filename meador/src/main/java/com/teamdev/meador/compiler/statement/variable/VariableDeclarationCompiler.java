package com.teamdev.meador.compiler.statement.variable;

import com.teamdev.fsm.InputSequence;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.fsmimpl.variable.VariableDeclarationFSM;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.variable.VariableHolder;

import java.util.Optional;

import static com.teamdev.meador.compiler.StatementType.EXPRESSION;

/**
 * {@link StatementCompiler} implementation for variable declaration.
 */
public class VariableDeclarationCompiler implements StatementCompiler {
    private final StatementCompilerFactory factory;

    public VariableDeclarationCompiler(
            StatementCompilerFactory factory) {
        this.factory = factory;
    }

    @Override
    public Optional<Command> compile(InputSequence input) throws CompilingException {
        var variableFSM = VariableDeclarationFSM.create((inputSequence, outputSequence) -> {

            var optionalCommand = factory.create(EXPRESSION).compile(inputSequence);

            optionalCommand.ifPresent(outputSequence::setCommand);

            return optionalCommand.isPresent();
        });

        var variable = new VariableHolder();

        if (variableFSM.accept(input, variable)) {
            return Optional.of(runtimeEnvironment -> {

                runtimeEnvironment.stack()
                        .create();

                variable.command()
                        .execute(runtimeEnvironment);

                runtimeEnvironment.memory()
                        .putVariable(variable.name(),
                                runtimeEnvironment.stack()
                                        .pop()
                                        .popResult());
            });
        }

        return Optional.empty();
    }
}
