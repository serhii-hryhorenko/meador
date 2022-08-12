package com.teamdev.meador.programelement.datastructure;

import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompiler;
import com.teamdev.meador.programelement.datastructure.fsmimpl.DataStructureFieldReferenceMachine;
import com.teamdev.meador.programelement.datastructure.fsmimpl.FieldReferenceOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.evaluation.TypeMismatchException;
import com.teamdev.runtime.evaluation.operandtype.DataStructureInitializationException;
import com.teamdev.runtime.evaluation.operandtype.DataStructureValueVisitor;
import com.teamdev.runtime.evaluation.operandtype.Value;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for providing access to data structure's field
 * values stored at {@link com.teamdev.runtime.Memory}.
 */
public class FieldValueCompiler implements ProgramElementCompiler {

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws SyntaxException {
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

                try {
                    var field = visitor.value()
                                       .getField(outputChain.fieldName());

                    runtimeEnvironment.stack().peek().pushOperand(field.getValue());
                } catch (DataStructureInitializationException e) {
                    throw new MeadorRuntimeException(e.getMessage());
                }
            });
        }

        return Optional.empty();
    }
}
