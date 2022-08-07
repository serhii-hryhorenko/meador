package com.teamdev.meador.programelement.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.programelement.CompilingException;
import com.teamdev.meador.programelement.ProgramElementCompiler;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.datastructure.fsmimpl.DataStructureDeclarationMachine;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.evaluation.operandtype.DataStructureTemplate;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for compiling structure declaration statements.
 * <br/>
 * Data structure declaration grammar reference:
 * <pre>
 *     Point { x, y };
 *     Person {name, age, job};
 * </pre>
 *
 * Parsed structure template is being put in {@link com.teamdev.runtime.Memory}.
 */
public class DataStructureDeclarationCompiler implements ProgramElementCompiler {

    private final ProgramElementCompilerFactory factory;

    public DataStructureDeclarationCompiler(ProgramElementCompilerFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        var dataStructure = new DataStructureTemplate();

        if (DataStructureDeclarationMachine.create(factory)
                .accept(reader, dataStructure)) {
            return Optional.of(runtimeEnvironment -> runtimeEnvironment.memory()
                                                                       .putDataStructureTemplate(
                                                                               dataStructure));
        }

        return Optional.empty();
    }
}
