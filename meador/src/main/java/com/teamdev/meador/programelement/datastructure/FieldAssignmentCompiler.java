package com.teamdev.meador.programelement.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompiler;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.datastructure.fsmimpl.FieldAssignmentMachine;
import com.teamdev.meador.programelement.datastructure.fsmimpl.FieldAssignmentOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.evaluation.TypeMismatchException;
import com.teamdev.runtime.evaluation.operandtype.DataStructureInitializationException;
import com.teamdev.runtime.evaluation.operandtype.DataStructureValueVisitor;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for compiling field value reassignment statements.
 */
public class FieldAssignmentCompiler implements ProgramElementCompiler {

    private final ProgramElementCompilerFactory factory;

    public FieldAssignmentCompiler(ProgramElementCompilerFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws SyntaxException {
        var context = new FieldAssignmentOutputChain();

        if (FieldAssignmentMachine.create(factory)
                .accept(reader, context)) {
            return Optional.of(runtimeEnvironment -> {
                var optionalValue = runtimeEnvironment.memory()
                                                      .getVariable(context.field()
                                                                          .variableName());

                var visitor = new DataStructureValueVisitor();

                try {
                    optionalValue.acceptVisitor(visitor);
                } catch (TypeMismatchException e) {
                    throw new MeadorRuntimeException(e.getMessage());
                }

                runtimeEnvironment.stack().create();
                context.command().execute(runtimeEnvironment);
                var value = runtimeEnvironment.stack()
                                                .pop()
                                                .popResult();

                try {
                    visitor.value()
                           .assignFieldValue(context.field()
                                                    .fieldName(), value);
                } catch (DataStructureInitializationException e) {
                    throw new MeadorRuntimeException(e.getMessage());
                }
            });
        }

        return Optional.empty();
    }
}
