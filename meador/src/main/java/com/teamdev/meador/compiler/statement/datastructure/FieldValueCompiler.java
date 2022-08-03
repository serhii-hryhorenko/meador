package com.teamdev.meador.compiler.statement.datastructure;

import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.fsmimpl.datastructure.DataStructureFieldMachine;
import com.teamdev.meador.fsmimpl.datastructure.FieldReferenceOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.value.type.Value;
import com.teamdev.runtime.value.type.datastructure.DataStructureValueVisitor;

import java.util.Optional;

public class FieldValueCompiler implements ProgramElementCompiler {

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        var outputChain = new FieldReferenceOutputChain();

        if (DataStructureFieldMachine.create().accept(reader, outputChain)) {
            return Optional.of(runtimeEnvironment -> {
                String name = outputChain.variableName();

                Value value = runtimeEnvironment.memory().getVariable(name);

                var visitor = new DataStructureValueVisitor();
                value.acceptVisitor(visitor);

                var field = visitor.value().getField(outputChain.fieldName());

                field.command().execute(runtimeEnvironment);
            });
        }

        return Optional.empty();
    }
}
