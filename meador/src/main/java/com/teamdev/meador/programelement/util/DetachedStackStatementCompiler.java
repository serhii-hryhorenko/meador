package com.teamdev.meador.programelement.util;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompiler;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.ShuntingYard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Compiles a certain Meador program element on detached {@link ShuntingYard} and puts a result
 * of computation on a top stack.
 */
public class DetachedStackStatementCompiler implements ProgramElementCompiler {

    private final StateAcceptor<List<Command>, SyntaxException> machine;

    public DetachedStackStatementCompiler(
            StateAcceptor<List<Command>, SyntaxException> machine) {
        this.machine = Preconditions.checkNotNull(machine);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws SyntaxException {
        List<Command> commands = new ArrayList<>();

        if (machine.accept(reader, commands)) {
            return Optional.of(runtimeEnvironment -> {

                runtimeEnvironment.stack()
                        .create();

                for (var command : commands) {
                    command.execute(runtimeEnvironment);
                }

                var result = runtimeEnvironment.stack()
                                               .pop()
                                               .popResult();

                runtimeEnvironment.stack()
                                  .peek()
                                  .pushOperand(result);
            });
        }

        return Optional.empty();
    }
}
