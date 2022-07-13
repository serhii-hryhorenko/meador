package com.teamdev.meador.compiler.statement.datastructure;

import com.teamdev.fsm.InputSequence;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.meador.fsmimpl.datastructure.FieldAssignmentContext;
import com.teamdev.meador.fsmimpl.datastructure.FieldValueFSM;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.type.DataStructureVisitor;

import java.util.Optional;

public class FieldValueCompiler implements StatementCompiler {

    @Override
    public Optional<Command> compile(InputSequence inputSequence) throws CompilingException {
        var context = new FieldAssignmentContext();

        if (FieldValueFSM.create().accept(inputSequence, context)) {
            return Optional.of(runtimeEnvironment -> {
                var optionalStructure = runtimeEnvironment.memory().getVariable(context.structureName());

                var visitor = new DataStructureVisitor();

                optionalStructure.orElseThrow().acceptVisitor(visitor);

                var fieldValue = visitor.value()
                        .getField(context.fieldName())
                        .orElseThrow()
                        .command();

                fieldValue.execute(runtimeEnvironment);
            });
        }

        return Optional.empty();
    }
}
