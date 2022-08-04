package com.teamdev.meador.compiler.statement.expression.relative;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.ProgramElementCompilerFactoryImpl;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.fsmimpl.expression.relative.RelationalExpressionOutputChain;
import com.teamdev.meador.fsmimpl.expression.relative.RelationalExpressionMachine;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.type.Value;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for compiling Meador relational expressions.
 */
public class RelationalExpressionCompiler implements ProgramElementCompiler {
    private final ProgramElementCompilerFactory compilerFactory;

    public RelationalExpressionCompiler(ProgramElementCompilerFactory factory) {
        this.compilerFactory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        var relationalExpressionFSM = RelationalExpressionMachine.create(compilerFactory);

        var context = new RelationalExpressionOutputChain();

        if (relationalExpressionFSM.accept(reader, context)) {
            return Optional.of(runtimeEnvironment -> {
                runtimeEnvironment.stack().create();

                context.left().execute(runtimeEnvironment);

                Value leftValue = runtimeEnvironment.stack().pop().popResult();

                runtimeEnvironment.stack().create();

                context.right().execute(runtimeEnvironment);

                Value rightValue = runtimeEnvironment.stack().pop().popResult();

                runtimeEnvironment.stack().create();
                runtimeEnvironment.stack().peek().pushOperand(leftValue);
                runtimeEnvironment.stack().peek().pushOperand(rightValue);
                runtimeEnvironment.stack().peek().pushOperator(context.operator());

                Value result = runtimeEnvironment.stack().pop().popResult();
                runtimeEnvironment.stack().peek().pushOperand(result);
            });
        }

        return Optional.empty();
    }
}
