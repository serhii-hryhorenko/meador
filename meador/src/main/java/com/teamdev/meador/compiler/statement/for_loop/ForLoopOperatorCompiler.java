package com.teamdev.meador.compiler.statement.for_loop;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.fsmimpl.for_loop.ForLoopOperatorFSM;
import com.teamdev.meador.fsmimpl.for_loop.ForLoopOperatorOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.RuntimeEnvironment;
import com.teamdev.runtime.value.type.BooleanVisitor;
import com.teamdev.runtime.value.type.Value;

import java.util.Optional;

/**
 * {@link StatementCompiler} implementation for creating commands of Meador {@code for} loops.
 * Variable declaration is required.
 * The loop body is not executed if repeat condition is failed.
 * Update variable statement is a usual variable declaration.
 * See details {@link ForLoopOperatorFSM}.
 */
public class ForLoopOperatorCompiler implements StatementCompiler {

    private final StatementCompilerFactory factory;

    public ForLoopOperatorCompiler(StatementCompilerFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        var outputChain = new ForLoopOperatorOutputChain();

        if (ForLoopOperatorFSM.create(factory).accept(reader, outputChain)) {
            return Optional.of(new Command() {
                @Override
                public void execute(RuntimeEnvironment runtimeEnvironment) {
                    outputChain.variableDeclaration().execute(runtimeEnvironment);

                    while (checkCondition(runtimeEnvironment, outputChain.repeatCondition())) {
                        outputChain.loopBody().execute(runtimeEnvironment);
                        outputChain.updateVariableStatement().execute(runtimeEnvironment);
                    }
                }

                private boolean checkCondition(RuntimeEnvironment runtimeEnvironment, Command booleanExpression) {
                    runtimeEnvironment.stack().create();
                    booleanExpression.execute(runtimeEnvironment);
                    Value condition = runtimeEnvironment.stack().peek().popResult();

                    var visitor = new BooleanVisitor();
                    condition.acceptVisitor(visitor);

                    return visitor.value();
                }
            });
        }

        return Optional.empty();
    }
}
