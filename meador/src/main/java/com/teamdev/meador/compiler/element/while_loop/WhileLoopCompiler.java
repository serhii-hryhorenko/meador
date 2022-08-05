package com.teamdev.meador.compiler.element.while_loop;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.fsmimpl.while_loop.WhileLoopMachine;
import com.teamdev.meador.fsmimpl.while_loop.WhileLoopOutputChain;
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
        WhileLoopOutputChain outputChain = new WhileLoopOutputChain();

        if (WhileLoopMachine.create(factory)
                .accept(reader, outputChain)) {
            return Optional.of(new Command() {
                @Override
                public void execute(RuntimeEnvironment runtimeEnvironment) throws
                                                                           MeadorRuntimeException {
                    int iterations = 0;

                    while (checkCondition(runtimeEnvironment, outputChain.condition())) {
                        outputChain.loopBodyStatements()
                                   .execute(runtimeEnvironment);

                        if (++iterations == RuntimeEnvironment.MAX_LOOP_ITERATIONS) {
                            throw new MeadorRuntimeException("Infinite while loop detected.");
                        }
                    }
                }
            });
        }

        return Optional.empty();
    }
}
