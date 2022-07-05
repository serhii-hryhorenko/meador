package com.teamdev.meador.compiler;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequence;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.math.type.Value;
import com.teamdev.meador.runtime.Command;
import com.teamdev.meador.runtime.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class CommandListMachineCompiler implements StatementCompiler {

    private final StateAcceptor<List<Command>, CompilingException> machine;
    private final BiConsumer<RuntimeEnvironment, Value> resultApplier;

    CommandListMachineCompiler(StateAcceptor<List<Command>, CompilingException> machine,
                               BiConsumer<RuntimeEnvironment, Value> resultApplier) {

        this.machine = Preconditions.checkNotNull(machine);
        this.resultApplier = Preconditions.checkNotNull(resultApplier);
    }

    @Override
    public Optional<Command> compile(InputSequence inputSequence) throws CompilingException {

        List<Command> outputSequence = new ArrayList<>();

        if (machine.accept(inputSequence, outputSequence)) {
            return Optional.of(runtimeEnvironment -> {

                runtimeEnvironment.stack().create();

                outputSequence.forEach(command -> command.execute(runtimeEnvironment));

                var yard = runtimeEnvironment.stack().pop();

                resultApplier.accept(runtimeEnvironment, yard.popResult());
            });
        }

        return Optional.empty();
    }
}
