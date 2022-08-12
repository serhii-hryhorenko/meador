package com.teamdev.meador.programelement.forloop;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompiler;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.forloop.fsmimpl.ForLoopOperatorMachine;
import com.teamdev.meador.programelement.forloop.fsmimpl.ForLoopOperatorOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.RuntimeEnvironment;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for creating commands of Meador {@code for} loops.
 * Variable declaration is required.
 * The loop body is not executed if repeat condition is false.
 * Update variable statement is a usual variable declaration.
 * See details {@link ForLoopOperatorMachine}.
 */
public class ForLoopOperatorCompiler implements ProgramElementCompiler {

    private final ProgramElementCompilerFactory factory;

    public ForLoopOperatorCompiler(ProgramElementCompilerFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws SyntaxException {
        var outputChain = new ForLoopOperatorOutputChain();

        if (ForLoopOperatorMachine.create(factory)
                .accept(reader, outputChain)) {

            return Optional.of(runtimeEnvironment -> {
                outputChain.variableDeclaration()
                           .execute(runtimeEnvironment);

                var iterations = 0;

                while (outputChain.repeatCondition().checkCondition(runtimeEnvironment)) {
                    outputChain.loopBody()
                               .execute(runtimeEnvironment);

                    outputChain.updateVariableStatement()
                               .execute(runtimeEnvironment);

                    if (++iterations == RuntimeEnvironment.MAX_LOOP_ITERATIONS) {
                        throw new MeadorRuntimeException("Infinite for loop detected.");
                    }
                }
            });
        }

        return Optional.empty();
    }
}
