package com.teamdev.meador.programelement.variable;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.programelement.CompilingException;
import com.teamdev.meador.programelement.ProgramElementCompiler;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.util.CompileStatementAcceptor;
import com.teamdev.meador.programelement.variable.fsmimpl.VariableDeclarationMachine;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.evaluation.operandtype.Value;
import com.teamdev.runtime.variable.VariableHolder;

import java.util.Optional;

import static com.teamdev.meador.programelement.ProgramElement.EXPRESSION;

/**
 * {@link ProgramElementCompiler} implementation for variable declaration and reassignment.
 */
public class VariableAssignmentCompiler implements ProgramElementCompiler {

    private final ProgramElementCompilerFactory factory;

    public VariableAssignmentCompiler(ProgramElementCompilerFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        var variableMachine = VariableDeclarationMachine.create(
                new CompileStatementAcceptor<>(factory, EXPRESSION, VariableHolder::setCommand));

        var builder = new VariableHolder();

        if (variableMachine.accept(reader, builder)) {
            return Optional.of(runtimeEnvironment -> {

                runtimeEnvironment.stack()
                        .create();
                builder.command()
                       .execute(runtimeEnvironment);
                Value variableValue = runtimeEnvironment.stack()
                                                        .pop()
                                                        .popResult();

                runtimeEnvironment.memory()
                                  .putVariable(builder.name(), variableValue);
            });
        }

        return Optional.empty();
    }
}
