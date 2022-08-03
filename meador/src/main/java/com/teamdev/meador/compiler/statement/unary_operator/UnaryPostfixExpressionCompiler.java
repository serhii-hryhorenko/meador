package com.teamdev.meador.compiler.statement.unary_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.ProgramElementCompilerFactoryImpl;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.compiler.statement.variable.VariableValueCompiler;
import com.teamdev.meador.fsmimpl.unary_operator.PostfixUnaryOperatorMachine;
import com.teamdev.meador.fsmimpl.unary_operator.UnaryExpressionOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.operator.AbstractOperatorFactory;
import com.teamdev.runtime.value.operator.unaryoperator.AbstractUnaryOperator;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for creating command of unary expressions with {@link AbstractUnaryOperator} postfix position.
 * This kind of operations <b>always</b> changes the variable value.
 */
public class UnaryPostfixExpressionCompiler implements ProgramElementCompiler {

    private final ProgramElementCompilerFactoryImpl statementCompilerFactory;
    private final AbstractOperatorFactory<AbstractUnaryOperator> unaryOperatorFactory;

    public UnaryPostfixExpressionCompiler(ProgramElementCompilerFactoryImpl statementCompilerFactory,
                                          AbstractOperatorFactory<AbstractUnaryOperator> unaryOperatorFactory) {

        this.statementCompilerFactory = Preconditions.checkNotNull(statementCompilerFactory);
        this.unaryOperatorFactory = Preconditions.checkNotNull(unaryOperatorFactory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        UnaryExpressionOutputChain outputChain = new UnaryExpressionOutputChain();
        if (PostfixUnaryOperatorMachine.create(statementCompilerFactory, unaryOperatorFactory)
                .accept(reader, outputChain)) {
            return Optional.of(runtimeEnvironment -> {
                var variableCommand = new VariableValueCompiler().compile(outputChain.variableName());

                variableCommand.ifPresent(command -> {
                    variableCommand.get().execute(runtimeEnvironment);

                    runtimeEnvironment.memory().putVariable(outputChain.variableName(),
                            outputChain.unaryOperator().apply(runtimeEnvironment.stack().peek().peekOperand()));
                });
            });
        }
        return Optional.empty();
    }
}
