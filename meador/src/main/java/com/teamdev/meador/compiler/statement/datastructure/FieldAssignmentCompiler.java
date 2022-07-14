package com.teamdev.meador.compiler.statement.datastructure;

import com.teamdev.fsm.InputSequence;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.fsmimpl.datastructure.FieldAssignmentFSM;
import com.teamdev.meador.fsmimpl.memory_call.MemoryValueContext;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.type.DataStructureVisitor;

import java.util.Optional;

public class FieldAssignmentCompiler implements StatementCompiler {

    private final StatementCompilerFactory factory;

    public FieldAssignmentCompiler(StatementCompilerFactory factory) {
        this.factory = factory;
    }

    @Override
    public Optional<Command> compile(InputSequence inputSequence) throws CompilingException {
        var context = new MemoryValueContext();
        if (FieldAssignmentFSM.create(factory).accept(inputSequence, context)) {
            return Optional.of(runtimeEnvironment -> {
                var optionalValue = runtimeEnvironment.memory().getVariable(context.variableName());

                var visitor = new DataStructureVisitor();
                optionalValue.orElseThrow().acceptVisitor(visitor);

                visitor.value().assignFieldValue(context.fieldName(), context.command());
            });
        }

        return Optional.empty();
    }
}
