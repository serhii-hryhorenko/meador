package com.teamdev.meador.compiler.statement.switch_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequence;
import com.teamdev.math.type.Value;
import com.teamdev.meador.StatementCompilerFactoryImpl;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.meador.fsmimpl.switch_operator.SwitchFSM;
import com.teamdev.meador.runtime.Command;

import java.util.Optional;

/**
 * {@link StatementCompiler} implementation for {@code switch} Meador operator.
 */
public final class SwitchOperatorCompiler implements StatementCompiler {
    private final StatementCompilerFactoryImpl compilerFactory;

    public SwitchOperatorCompiler(StatementCompilerFactoryImpl compilerFactory) {
        this.compilerFactory = Preconditions.checkNotNull(compilerFactory);
    }

    @Override
    public Optional<Command> compile(InputSequence inputSequence) throws CompilingException {
        var context = new SwitchContext();

        if (SwitchFSM.create(compilerFactory).accept(inputSequence, context)) {
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
