package com.teamdev.meador.compiler.element.datastructure;

import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.fsmimpl.datastructure.DataStructureFieldReferenceMachine;
import com.teamdev.meador.fsmimpl.datastructure.FieldReferenceOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.evaluation.TypeMismatchException;
import com.teamdev.runtime.evaluation.operandtype.Value;
import com.teamdev.runtime.evaluation.operandtype.DataStructureValueVisitor;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for providing access to data structure's field
 * values stored at {@link com.teamdev.runtime.Memory}.
 */
public class FieldValueCompiler implements ProgramElementCompiler {

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        var outputChain = new FieldReferenceOutputChain();

        if (DataStructureFieldReferenceMachine.create()
                .accept(reader, outputChain)) {
            return Optional.of(runtimeEnvironment -> {
                String name = outputChain.variableName();

                Value value = runtimeEnvironment.memory()
                                                .getVariable(name);

                var visitor = new DataStructureValueVisitor();

                try {
                    value.acceptVisitor(visitor);
                } catch (TypeMismatchException e) {
                    throw new MeadorRuntimeException(e.getMessage());
                }

                var field = visitor.value()
                                   .getField(outputChain.fieldName());

                runtimeEnvironment.stack().peek().pushOperand(field.getValue());
            });
        }

        return Optional.empty();
    }
}
