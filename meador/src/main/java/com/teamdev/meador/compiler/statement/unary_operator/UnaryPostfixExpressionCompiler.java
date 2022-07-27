package com.teamdev.meador.compiler.statement.unary_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.StatementCompilerFactoryImpl;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.meador.compiler.statement.variable.VariableValueCompiler;
import com.teamdev.meador.fsmimpl.unary_operator.PostfixOperatorFSM;
import com.teamdev.meador.fsmimpl.unary_operator.UnaryExpressionOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.operator.AbstractOperatorFactory;
import com.teamdev.runtime.value.operator.unaryoperator.AbstractUnaryOperator;

import java.util.Optional;

public class UnaryPostfixExpressionCompiler implements StatementCompiler {

    private final StatementCompilerFactoryImpl statementCompilerFactory;
    private final AbstractOperatorFactory<AbstractUnaryOperator> unaryOperatorFactory;

    public UnaryPostfixExpressionCompiler(StatementCompilerFactoryImpl statementCompilerFactory,
                                          AbstractOperatorFactory<AbstractUnaryOperator> unaryOperatorFactory) {

        this.statementCompilerFactory = Preconditions.checkNotNull(statementCompilerFactory);
        this.unaryOperatorFactory = Preconditions.checkNotNull(unaryOperatorFactory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader inputSequence) throws CompilingException {
        UnaryExpressionOutputChain outputChain = new UnaryExpressionOutputChain();
        if (PostfixOperatorFSM.create(statementCompilerFactory, unaryOperatorFactory)
                .accept(inputSequence, outputChain)) {
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
