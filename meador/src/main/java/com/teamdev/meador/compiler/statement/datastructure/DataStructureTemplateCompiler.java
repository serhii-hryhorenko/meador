package com.teamdev.meador.compiler.statement.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequence;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.fsmimpl.datastructure.DataStructureTemplateFSM;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.datastructure.DataStructureTemplate;

import java.util.Optional;

public class DataStructureTemplateCompiler implements StatementCompiler {

    private final StatementCompilerFactory factory;

    public DataStructureTemplateCompiler(StatementCompilerFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequence inputSequence) throws CompilingException {
        var dataStructure = new DataStructureTemplate();

        if (DataStructureTemplateFSM.create(factory).accept(inputSequence, dataStructure)) {
            return Optional.of(runtimeEnvironment -> runtimeEnvironment.addStructureTemplate(dataStructure));
        }

        return Optional.empty();
    }
}