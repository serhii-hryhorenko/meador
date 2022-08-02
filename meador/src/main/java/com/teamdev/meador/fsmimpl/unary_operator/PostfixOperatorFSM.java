package com.teamdev.meador.fsmimpl.unary_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.expression.OperatorAcceptor;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.runtime.value.operator.AbstractOperatorFactory;
import com.teamdev.runtime.value.operator.unaryoperator.AbstractUnaryOperator;

/**
 * {@link FiniteStateMachine} implementation for recognizing unary expressions with an {@link AbstractUnaryOperator} postfix
 * position.
 * Parses only the variable name, not a value.
 */
public class PostfixOperatorFSM extends FiniteStateMachine<UnaryExpressionOutputChain, CompilingException> {

    public static PostfixOperatorFSM create(StatementCompilerFactory compilerFactory,
                                            AbstractOperatorFactory<AbstractUnaryOperator> operatorFactory) {
        Preconditions.checkNotNull(compilerFactory, operatorFactory);

        var exceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var expression = new State.Builder<UnaryExpressionOutputChain, CompilingException>()
                .setName("VARIABLE NAME")
                .setAcceptor((reader, outputSequence) -> TextIdentifierFSM.acceptIdentifier(reader, outputSequence,
                        UnaryExpressionOutputChain::setVariableName,
                        exceptionThrower))
                .setTemporary()
                .build();

        var postfixOperator = new State.Builder<UnaryExpressionOutputChain, CompilingException>()
                .setName("POSTFIX OPERATOR")
                .setAcceptor(new OperatorAcceptor<>(operatorFactory, UnaryExpressionOutputChain::setUnaryOperator))
                .setFinal()
                .build();

        return new PostfixOperatorFSM(TransitionMatrix.chainedTransitions(expression, postfixOperator), exceptionThrower);
    }

    private PostfixOperatorFSM(TransitionMatrix<UnaryExpressionOutputChain, CompilingException> transitionMatrix,
                               ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
