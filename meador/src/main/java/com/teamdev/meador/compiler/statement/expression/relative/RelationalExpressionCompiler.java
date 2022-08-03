package com.teamdev.meador.compiler.statement.expression.relative;

import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.ProgramElementCompilerFactoryImpl;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.fsmimpl.expression.relative.RelationalExpressionContext;
import com.teamdev.meador.fsmimpl.expression.relative.RelationalExpressionMachine;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.type.Value;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for compiling Meador relational expressions.
 */
public class RelationalExpressionCompiler implements ProgramElementCompiler {
    private final ProgramElementCompilerFactoryImpl compilerFactory;

    public RelationalExpressionCompiler(ProgramElementCompilerFactoryImpl compilerFactory) {
        this.compilerFactory = compilerFactory;
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        var relationalExpressionFSM = RelationalExpressionMachine.create(compilerFactory);

        var context = new RelationalExpressionContext();

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
