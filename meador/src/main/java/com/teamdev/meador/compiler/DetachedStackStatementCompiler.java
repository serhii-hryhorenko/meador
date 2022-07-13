package com.teamdev.meador.compiler;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequence;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.ShuntingYard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Compiles a certain Meador statement on detached {@link ShuntingYard} and puts a result
 * of computation on a top stack.
 */
public class DetachedStackStatementCompiler implements StatementCompiler {
    private final StateAcceptor<List<Command>, CompilingException> machine;

    public DetachedStackStatementCompiler(StateAcceptor<List<Command>, CompilingException> machine) {
        this.machine = Preconditions.checkNotNull(machine);
    }

    @Override
    public Optional<Command> compile(InputSequence inputSequence) throws CompilingException {

        List<Command> outputSequence = new ArrayList<>();

        if (machine.accept(inputSequence, outputSequence)) {
            return Optional.of(runtimeEnvironment -> {

                runtimeEnvironment.stack().create();

                outputSequence.forEach(command -> command.execute(runtimeEnvironment));

                var result = runtimeEnvironment.stack().pop().popResult();

                runtimeEnvironment.stack().peek().pushOperand(result);
            });
        }

        return Optional.empty();
    }
}
