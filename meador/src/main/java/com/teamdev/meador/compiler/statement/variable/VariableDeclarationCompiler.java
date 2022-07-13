package com.teamdev.meador.compiler.statement.variable;

import com.teamdev.fsm.InputSequence;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.fsmimpl.variable.VariableDeclarationFSM;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.variable.VariableHolder;

import java.util.Objects;
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
        var variable = VariableDeclarationFSM.create((inputSequence, outputSequence) -> {

            var optionalCommand = factory.create(EXPRESSION)
                    .compile(inputSequence);

            optionalCommand.ifPresent(outputSequence::setCommand);

            return optionalCommand.isPresent();
        });

        var builder = new VariableHolder();

        if (variable.accept(input, builder)) {
            return Optional.of(runtimeEnvironment -> {

                runtimeEnvironment.stack()
                        .create();

                builder.command()
                        .execute(runtimeEnvironment);

                runtimeEnvironment.memory()
                        .putVariable(builder.name(),
                                runtimeEnvironment.stack()
                                        .pop()
                                        .popResult());
            });
        }

        return Optional.empty();
    }

    public StatementCompilerFactory factory() {
        return factory;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (VariableDeclarationCompiler) obj;
        return Objects.equals(this.factory, that.factory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factory);
    }

    @Override
    public String toString() {
        return "VariableDeclarationCompiler[" +
                "factory=" + factory + ']';
    }

}
