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
public final class SwitchOperatorCompiler implements ProgramElementCompiler {
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
                context.value().execute(runtimeEnvironment);

                Value matchedValue = runtimeEnvironment.stack().pop().popResult();

                context.options().stream()
                        .filter(switchOptionContext -> {
                            Command condition = switchOptionContext.condition();
                            runtimeEnvironment.stack().create();
                            condition.execute(runtimeEnvironment);
                            Value conditionValue = runtimeEnvironment.stack().pop().popResult();
                            return matchedValue.equals(conditionValue);
                        })
                        .findFirst()
                        .ifPresentOrElse(switchOptionContext -> switchOptionContext.statement().execute(runtimeEnvironment),
                                () -> context.defaultCommand().execute(runtimeEnvironment));
            });
        }
        return Optional.empty();
    }
}
