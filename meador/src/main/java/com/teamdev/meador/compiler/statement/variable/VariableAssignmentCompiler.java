package com.teamdev.meador.compiler.statement.variable;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.fsmimpl.variable.VariableDeclarationMachine;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.variable.VariableHolder;

import java.util.Optional;

import static com.teamdev.meador.compiler.ProgramElement.EXPRESSION;

/**
 * {@link ProgramElementCompiler} implementation for variable declaration.
 */
public class VariableAssignmentCompiler implements ProgramElementCompiler {
    private final ProgramElementCompilerFactory factory;

    public VariableAssignmentCompiler(ProgramElementCompilerFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        var variableMachine = VariableDeclarationMachine.create(
                new CompileStatementAcceptor<>(factory,
                        EXPRESSION,
                        VariableHolder::setCommand));



        var builder = new VariableHolder();

        if (variableMachine.accept(reader, builder)) {
            return Optional.of(runtimeEnvironment -> {

                runtimeEnvironment.stack().create();

                builder.command().execute(runtimeEnvironment);

                runtimeEnvironment.memory()
                        .putVariable(builder.name(),
                                runtimeEnvironment.stack()
                                        .pop()
                                        .popResult());
            });
        }

        return Optional.empty();
    }
}
