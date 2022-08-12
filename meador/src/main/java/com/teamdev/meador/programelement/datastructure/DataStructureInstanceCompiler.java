package com.teamdev.meador.programelement.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompiler;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.datastructure.fsmimpl.DataStructureInstanceMachine;
import com.teamdev.meador.programelement.datastructure.fsmimpl.DataStructureOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.evaluation.operandtype.DataStructureHolder;
import com.teamdev.runtime.evaluation.operandtype.DataStructureInitializationException;
import com.teamdev.runtime.evaluation.operandtype.DataStructureValue;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for compiling structure instantiating statements.
 * <br/>
 * Data structure instantiating grammar reference:
 * <pre>
 *     Point { x, y };
 *
 *     point = Point { 1, 2 };
 *
 *     print(point.x, point.y);
 * </pre>
 *
 * Parsed structure instance is represented as {@link DataStructureValue}.
 */
public class DataStructureInstanceCompiler implements ProgramElementCompiler {

    private final ProgramElementCompilerFactory factory;

    public DataStructureInstanceCompiler(ProgramElementCompilerFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader inputSequence) throws SyntaxException {
        var dataStructureContext = new DataStructureOutputChain();

        if (DataStructureInstanceMachine.create(factory)
                .accept(inputSequence, dataStructureContext)) {
            return Optional.of(runtimeEnvironment -> {
                var template = runtimeEnvironment.memory()
                                                 .getDataStructureTemplate(
                                                         dataStructureContext.templateName());

                if (template.fieldNumber() != dataStructureContext.fieldValues()
                                                                  .size()) {
                    throw new MeadorRuntimeException(
                            "The number of parsed fields doesn't match the template's from memory.");
                }

                var implementation = new DataStructureHolder(template);

                for (var value : dataStructureContext.fieldValues()) {
                    runtimeEnvironment.stack().create();
                    value.execute(runtimeEnvironment);

                    try {
                        implementation.assignFieldValue(runtimeEnvironment.stack().pop().popResult());
                    } catch (DataStructureInitializationException e) {
                        throw new MeadorRuntimeException(e.getMessage());
                    }
                }

                runtimeEnvironment.stack()
                                  .peek()
                                  .pushOperand(new DataStructureValue(implementation));
            });
        }

        return Optional.empty();
    }
}
