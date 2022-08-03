package com.teamdev.meador.compiler.statement.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.fsmimpl.datastructure.DataStructureOutputChain;
import com.teamdev.meador.fsmimpl.datastructure.DataStructureInstanceMachine;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.type.datastructure.DataStructureHolder;
import com.teamdev.runtime.value.type.datastructure.DataStructureValue;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for compiling data structure instantiating statements.
 */
public class DataStructureInstanceCompiler implements ProgramElementCompiler {
    private final ProgramElementCompilerFactory factory;

    public DataStructureInstanceCompiler(ProgramElementCompilerFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader inputSequence) throws CompilingException {
        var dataStructureContext = new DataStructureOutputChain();

        if (DataStructureInstanceMachine.create(factory).accept(inputSequence, dataStructureContext)) {
            return Optional.of(runtimeEnvironment -> {
                var optionalTemplate = runtimeEnvironment.memory()
                        .getDataStructureTemplate(dataStructureContext.templateName());

                var template = optionalTemplate.orElseThrow();

                if (template.fieldNumber() != dataStructureContext.fieldValues().size()) {
                    throw new IllegalStateException("The number of parsed fields don't match the template's from memory.");
                }

                var implementation = new DataStructureHolder(template);

                dataStructureContext.fieldValues().forEach(implementation::assignFieldValue);

                runtimeEnvironment.stack().peek().pushOperand(new DataStructureValue(implementation));
            });
        }

        return Optional.empty();
    }
}
