package com.teamdev.meador.compiler.statement.relative_expr;

import com.teamdev.fsm.InputSequence;
import com.teamdev.meador.StatementCompilerFactoryImpl;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.meador.fsmimpl.util.RelationalExpressionFSM;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.type.Value;

import java.util.Optional;

/**
 * {@link StatementCompiler} implementation for compiling Meador relational expressions.
 */
public class RelationalExpressionCompiler implements StatementCompiler {
    private final StatementCompilerFactoryImpl compilerFactory;

    public RelationalExpressionCompiler(StatementCompilerFactoryImpl compilerFactory) {
        this.compilerFactory = compilerFactory;
    }

    @Override
    public Optional<Command> compile(InputSequence inputSequence) throws CompilingException {
        var relationalExpressionFSM = RelationalExpressionFSM.create(compilerFactory);

        var context = new RelationalExpressionContext();

        if (relationalExpressionFSM.accept(inputSequence, context)) {
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
