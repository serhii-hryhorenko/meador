package com.teamdev.meador.programelement.unaryoperator.fsmimpl;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.expression.OperatorAcceptor;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.programelement.CompilingException;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.runtime.evaluation.operator.AbstractOperatorFactory;
import com.teamdev.runtime.evaluation.operator.AbstractUnaryOperator;

/**
 * {@link FiniteStateMachine} implementation for recognizing unary expressions with an {@link
 * AbstractUnaryOperator} prefix
 * position.
 * Parses only the variable name, not a value.
 */
public class PrefixUnaryOperatorMachine extends FiniteStateMachine<UnaryExpressionOutputChain, CompilingException> {

    private PrefixUnaryOperatorMachine(
            TransitionMatrix<UnaryExpressionOutputChain, CompilingException> transitionMatrix,
            ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }

    public static PrefixUnaryOperatorMachine create(ProgramElementCompilerFactory compilerFactory,
                                                    AbstractOperatorFactory<AbstractUnaryOperator> operatorFactory) {
        Preconditions.checkNotNull(compilerFactory, operatorFactory);

        var exceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var prefixOperator = new State.Builder<UnaryExpressionOutputChain, CompilingException>()
                .setName("PREFIX OPERATOR")
                .setAcceptor(new OperatorAcceptor<>(operatorFactory,
                                                    UnaryExpressionOutputChain::setUnaryOperator))
                .setTemporary()
                .build();

        var expression = new State.Builder<UnaryExpressionOutputChain, CompilingException>()
                .setName("VARIABLE NAME")
                .setAcceptor(
                        (reader, outputSequence) -> TextIdentifierMachine.acceptIdentifier(reader,
                                                                                           outputSequence,
                                                                                           UnaryExpressionOutputChain::setVariableName,
                                                                                           exceptionThrower))
                .setFinal()
                .build();

        return new PrefixUnaryOperatorMachine(
                TransitionMatrix.chainedTransitions(prefixOperator, expression),
                exceptionThrower);
    }
}
