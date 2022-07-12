package com.teamdev.meador.compiler.statement.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequence;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.fsmimpl.datastructure.DataStructureContext;
import com.teamdev.meador.fsmimpl.datastructure.DataStructureFSM;
import com.teamdev.meador.fsmimpl.datastructure.DataStructureHolder;
import com.teamdev.meador.runtime.Command;
import com.teamdev.meador.runtime.RuntimeEnvironment;

import java.util.Optional;

public class DataStructureCompiler implements StatementCompiler {

    private final StatementCompilerFactory factory;

    public DataStructureCompiler(StatementCompilerFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequence inputSequence) throws CompilingException {
        var dataStructureContext = new DataStructureContext();

        if (DataStructureFSM.create(factory).accept(inputSequence, dataStructureContext)) {
            return Optional.of(new Command() {
                @Override
                public void execute(RuntimeEnvironment runtimeEnvironment) {
                    var template = runtimeEnvironment.getStructureTemplate(dataStructureContext.templateName());

                    var implementation = new DataStructureHolder(template.orElseThrow());

                    dataStructureContext.fieldValues().forEach(implementation::assignFieldValue);

                    runtimeEnvironment.stack().peek().pushOperand(null);
                }
            });
        }
        return Optional.empty();
    }
}
