package com.teamdev.meador.compiler.statement.unary_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.ProgramElementCompilerFactoryImpl;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.compiler.statement.variable.VariableValueCompiler;
import com.teamdev.meador.fsmimpl.unary_operator.PrefixUnaryOperatorMachine;
import com.teamdev.meador.fsmimpl.unary_operator.UnaryExpressionOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.operator.AbstractOperatorFactory;
import com.teamdev.runtime.value.operator.unaryoperator.AbstractUnaryOperator;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for creating command of unary expressions with {@link AbstractUnaryOperator} prefix position.
 * These operations <b>can change</b> the variable value if the operator is supposed to do it.
 */
public class UnaryPrefixExpressionCompiler implements ProgramElementCompiler {

    private final ProgramElementCompilerFactoryImpl statementCompilerFactory;

    private final AbstractOperatorFactory<AbstractUnaryOperator> unaryOperatorFactory;

    public UnaryPrefixExpressionCompiler(ProgramElementCompilerFactoryImpl statementCompilerFactory,
                                         AbstractOperatorFactory<AbstractUnaryOperator> unaryOperatorFactory) {

        this.statementCompilerFactory = Preconditions.checkNotNull(statementCompilerFactory);
        this.unaryOperatorFactory = Preconditions.checkNotNull(unaryOperatorFactory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        UnaryExpressionOutputChain outputChain = new UnaryExpressionOutputChain();

        if (PrefixUnaryOperatorMachine.create(statementCompilerFactory, unaryOperatorFactory)
                .accept(reader, outputChain)) {

            return Optional.of(runtimeEnvironment -> {
                var variableCommand = new VariableValueCompiler().compile(outputChain.variableName());

                variableCommand.ifPresent(command -> {
                    variableCommand.get().execute(runtimeEnvironment);

                    var topStack = runtimeEnvironment.stack().peek();

                    var applied = outputChain.unaryOperator().apply(topStack.popOperand());

                    topStack.pushOperand(applied);

                    if (outputChain.unaryOperator().prefixFormMutatesVariable()) {
                        runtimeEnvironment.memory().putVariable(outputChain.variableName(), applied);
                    }
                });
            });
        }

        return Optional.empty();
    }
}