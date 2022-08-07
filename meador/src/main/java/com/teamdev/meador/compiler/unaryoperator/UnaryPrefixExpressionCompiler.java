package com.teamdev.meador.compiler.unaryoperator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.compiler.unaryoperator.fsmimpl.PrefixUnaryOperatorMachine;
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
 * {@link AbstractUnaryOperator} prefix position.
 * These operations <b>can change</b> the variable value if the operator is supposed to do it.
 */
public class UnaryPrefixExpressionCompiler implements ProgramElementCompiler {

    private final ProgramElementCompilerFactory statementCompilerFactory;

    private final AbstractOperatorFactory<AbstractUnaryOperator> unaryOperatorFactory;

    public UnaryPrefixExpressionCompiler(ProgramElementCompilerFactory statementCompilerFactory,
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
                var variableCommand = new VariableValueCompiler().compile(
                        outputChain.variableName());

                if (variableCommand.isPresent()) {
                    variableCommand.get()
                                   .execute(runtimeEnvironment);

                    var topStack = runtimeEnvironment.stack()
                                                     .peek();

                    try {
                        var applied = outputChain.unaryOperator()
                                                 .apply(topStack.popOperand());

                        topStack.pushOperand(applied);

                        if (outputChain.unaryOperator()
                                       .prefixFormMutatesVariable()) {
                            runtimeEnvironment.memory()
                                              .putVariable(outputChain.variableName(), applied);
                        }
                    } catch (TypeMismatchException e) {
                        throw new MeadorRuntimeException(e.getMessage());
                    }
                }
            });
        }

        return Optional.empty();
    }
}
