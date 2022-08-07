package com.teamdev.meador.compiler.unaryoperator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.compiler.unaryoperator.fsmimpl.PostfixUnaryOperatorMachine;
import com.teamdev.meador.compiler.unaryoperator.fsmimpl.UnaryExpressionOutputChain;
import com.teamdev.meador.compiler.variable.VariableValueCompiler;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.evaluation.TypeMismatchException;
import com.teamdev.runtime.evaluation.operator.AbstractOperatorFactory;
import com.teamdev.runtime.evaluation.operator.AbstractUnaryOperator;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for creating command of unary expressions with
 * {@link AbstractUnaryOperator} postfix position.
 * This kind of operations <b>always</b> changes the variable value.
 */
public class UnaryPostfixExpressionCompiler implements ProgramElementCompiler {

    private final ProgramElementCompilerFactory statementCompilerFactory;
    private final AbstractOperatorFactory<AbstractUnaryOperator> unaryOperatorFactory;

    public UnaryPostfixExpressionCompiler(ProgramElementCompilerFactory statementCompilerFactory,
                                          AbstractOperatorFactory<AbstractUnaryOperator> unaryOperatorFactory) {

        this.statementCompilerFactory = Preconditions.checkNotNull(statementCompilerFactory);
        this.unaryOperatorFactory = Preconditions.checkNotNull(unaryOperatorFactory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        var outputChain = new UnaryExpressionOutputChain();

        if (PostfixUnaryOperatorMachine.create(statementCompilerFactory, unaryOperatorFactory)
                .accept(reader, outputChain)) {
            return Optional.of(runtimeEnvironment -> {

                var variableCommand = new VariableValueCompiler().compile(
                        outputChain.variableName());

                if (variableCommand.isPresent()) {
                    variableCommand.get()
                                   .execute(runtimeEnvironment);

                    try {
                        runtimeEnvironment.memory()
                                          .putVariable(outputChain.variableName(),
                                                       outputChain.unaryOperator()
                                                                  .apply(runtimeEnvironment.stack()
                                                                                           .peek()
                                                                                           .peekOperand()));
                    } catch (TypeMismatchException e) {
                        throw new MeadorRuntimeException(e.getMessage());
                    }
                }
            });
        }
        return Optional.empty();
    }
}
