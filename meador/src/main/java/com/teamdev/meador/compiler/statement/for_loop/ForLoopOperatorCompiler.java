package com.teamdev.meador.compiler.statement.for_loop;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.fsmimpl.for_loop.ForLoopOperatorMachine;
import com.teamdev.meador.fsmimpl.for_loop.ForLoopOperatorOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.RuntimeEnvironment;
import com.teamdev.runtime.value.type.bool.BooleanValueVisitor;
import com.teamdev.runtime.value.type.Value;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for creating commands of Meador {@code for} loops.
 * Variable declaration is required.
 * The loop body is not executed if repeat condition is false.
 * Update variable statement is a usual variable declaration.
 * See details {@link ForLoopOperatorMachine}.
 */
public class ForLoopOperatorCompiler implements ProgramElementCompiler {
    private static final int MAX_ITERATIONS = 10000;

    private final ProgramElementCompilerFactory factory;

    public ForLoopOperatorCompiler(ProgramElementCompilerFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        var outputChain = new ForLoopOperatorOutputChain();

        if (ForLoopOperatorMachine.create(factory).accept(reader, outputChain)) {
            return Optional.of(new Command() {
                @Override
                public void execute(RuntimeEnvironment runtimeEnvironment) throws MeadorRuntimeException {
                    outputChain.variableDeclaration().execute(runtimeEnvironment);

                    int iterations = 0;

                    while (checkCondition(runtimeEnvironment, outputChain.repeatCondition())) {
                        outputChain.loopBody().execute(runtimeEnvironment);
                        outputChain.updateVariableStatement().execute(runtimeEnvironment);

                        if (++iterations == MAX_ITERATIONS) {
                            throw new MeadorRuntimeException("Infinite loop detected.");
                        }
                    }
                }

                private boolean checkCondition(RuntimeEnvironment runtimeEnvironment, Command booleanExpression) throws MeadorRuntimeException {
                    runtimeEnvironment.stack().create();
                    booleanExpression.execute(runtimeEnvironment);
                    Value condition = runtimeEnvironment.stack().pop().popResult();

                    var visitor = new BooleanValueVisitor();
                    condition.acceptVisitor(visitor);

                    return visitor.value();
                }
            });
        }

        return Optional.empty();
    }
}
