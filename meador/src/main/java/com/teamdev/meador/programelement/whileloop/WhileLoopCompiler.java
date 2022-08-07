package com.teamdev.meador.programelement.whileloop;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.programelement.CompilingException;
import com.teamdev.meador.programelement.ProgramElementCompiler;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.whileloop.fsmimpl.WhileLoopMachine;
import com.teamdev.meador.programelement.whileloop.fsmimpl.WhileLoopOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.RuntimeEnvironment;

import java.util.Optional;

public class WhileLoopCompiler implements ProgramElementCompiler {

    private final ProgramElementCompilerFactory factory;

    public WhileLoopCompiler(ProgramElementCompilerFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        var outputChain = new WhileLoopOutputChain();

        if (WhileLoopMachine.create(factory)
                .accept(reader, outputChain)) {

            return Optional.of(runtimeEnvironment -> {
                int iterations = 0;

                while (outputChain.condition().checkCondition(runtimeEnvironment)) {
                    outputChain.loopBodyStatements()
                               .execute(runtimeEnvironment);

                    if (++iterations == RuntimeEnvironment.MAX_LOOP_ITERATIONS) {
                        throw new MeadorRuntimeException("Infinite while loop detected.");
                    }
                }
            });
        }

        return Optional.empty();
    }
}
