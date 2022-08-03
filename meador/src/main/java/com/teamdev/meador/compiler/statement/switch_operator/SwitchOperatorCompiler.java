package com.teamdev.meador.compiler.statement.switch_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.ProgramElementCompilerFactoryImpl;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.fsmimpl.switch_operator.SwitchOperatorMachine;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.type.Value;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for {@code switch} Meador operator.
 */
public class SwitchOperatorCompiler implements ProgramElementCompiler {
    private final ProgramElementCompilerFactoryImpl compilerFactory;

    public SwitchOperatorCompiler(ProgramElementCompilerFactoryImpl compilerFactory) {
        this.compilerFactory = Preconditions.checkNotNull(compilerFactory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        var context = new SwitchOperatorOutputChain();

        if (SwitchOperatorMachine.create(compilerFactory).accept(reader, context)) {
            return Optional.of(runtimeEnvironment -> {

                runtimeEnvironment.stack().create();
                context.mappedValue().execute(runtimeEnvironment);
                Value mappedValue = runtimeEnvironment.stack().pop().popResult();

                for (var option : context.options()) {
                    var condition = option.condition();

                    runtimeEnvironment.stack().create();
                    condition.execute(runtimeEnvironment);
                    var conditionValue = runtimeEnvironment.stack().pop().popResult();

                    if (mappedValue.equals(conditionValue)) {
                        option.statement().execute(runtimeEnvironment);
                        return;
                    }
                }

                context.defaultCommand().execute(runtimeEnvironment);
            });
        }
        return Optional.empty();
    }
}
