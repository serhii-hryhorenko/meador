package com.teamdev.meador.compiler.statement.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequence;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.fsmimpl.datastructure.DataStructureContext;
import com.teamdev.meador.fsmimpl.datastructure.DataStructureFSM;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.datastructure.DataStructureHolder;
import com.teamdev.runtime.value.type.DataStructureValue;

import java.util.Optional;

/**
 * {@link StatementCompiler} implementation for compiling data structure instantiating statements.
 */
public class DataStructureCompiler implements StatementCompiler {

    private final StatementCompilerFactory factory;

    public DataStructureCompiler(StatementCompilerFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequence inputSequence) throws CompilingException {
        var dataStructureContext = new DataStructureContext();

        if (DataStructureFSM.create(factory).accept(inputSequence, dataStructureContext)) {
            return Optional.of(runtimeEnvironment -> {
                var optionalTemplate = runtimeEnvironment
                        .getStructureTemplate(dataStructureContext.templateName());

                var template = optionalTemplate.orElseThrow();

                if (template.fieldNumber() != dataStructureContext.fieldValues().size()) {
                    throw new RuntimeException();
                }

                var implementation = new DataStructureHolder(template);

                dataStructureContext.fieldValues().forEach(implementation::assignFieldValue);

                runtimeEnvironment.stack().peek().pushOperand(new DataStructureValue(implementation));
            });
        }
        return Optional.empty();
    }
}
