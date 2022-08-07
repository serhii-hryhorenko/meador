package com.teamdev.meador.programelement.switchoperator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.programelement.CompilingException;
import com.teamdev.meador.programelement.ProgramElementCompiler;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.switchoperator.fsmimpl.SwitchOperatorMachine;
import com.teamdev.meador.programelement.switchoperator.fsmimpl.SwitchOperatorOutputChain;
import com.teamdev.runtime.Command;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for {@code switch} Meador operator.
 */
public class SwitchOperatorCompiler implements ProgramElementCompiler {

    private final ProgramElementCompilerFactory compilerFactory;

    public SwitchOperatorCompiler(ProgramElementCompilerFactory compilerFactory) {
        this.compilerFactory = Preconditions.checkNotNull(compilerFactory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        var outputChain = new SwitchOperatorOutputChain();

        if (SwitchOperatorMachine.create(compilerFactory)
                .accept(reader, outputChain)) {
            return Optional.of(runtimeEnvironment -> {

                runtimeEnvironment.stack()
                        .create();
                outputChain.mappedValue()
                           .execute(runtimeEnvironment);
                var mappedValue = runtimeEnvironment.stack()
                                                    .pop()
                                                    .popResult();

                for (var option : outputChain.options()) {
                    var condition = option.condition();

                    runtimeEnvironment.stack()
                            .create();
                    condition.execute(runtimeEnvironment);
                    var conditionValue = runtimeEnvironment.stack()
                                                           .pop()
                                                           .popResult();

                    if (mappedValue.equals(conditionValue)) {
                        option.statement()
                              .execute(runtimeEnvironment);
                        return;
                    }
                }

                outputChain.defaultCommand()
                           .execute(runtimeEnvironment);
            });
        }

        return Optional.empty();
    }
}
