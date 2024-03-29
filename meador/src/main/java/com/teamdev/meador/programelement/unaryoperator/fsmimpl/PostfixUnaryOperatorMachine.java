package com.teamdev.meador.programelement.unaryoperator.fsmimpl;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.expression.OperatorAcceptor;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.runtime.evaluation.operator.AbstractOperatorFactory;
import com.teamdev.runtime.evaluation.operator.AbstractUnaryOperator;

/**
 * {@link FiniteStateMachine} implementation for recognizing unary expressions with an {@link
 * AbstractUnaryOperator} postfix
 * position.
 * Parses only the variable name, not a value.
 */
public class PostfixUnaryOperatorMachine extends FiniteStateMachine<UnaryExpressionOutputChain, SyntaxException> {

    private PostfixUnaryOperatorMachine(
            TransitionMatrix<UnaryExpressionOutputChain, SyntaxException> transitionMatrix,
            ExceptionThrower<SyntaxException> thrower) {
        super(transitionMatrix, thrower);
    }

    public static PostfixUnaryOperatorMachine create(ProgramElementCompilerFactory compilerFactory,
                                                     AbstractOperatorFactory<AbstractUnaryOperator> operatorFactory) {
        Preconditions.checkNotNull(compilerFactory, operatorFactory);

        var exceptionThrower = new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize postfix unary operator."));

        var expression = new State.Builder<UnaryExpressionOutputChain, SyntaxException>()
                .setName("VARIABLE NAME")
                .setAcceptor(
                        (reader, outputSequence) -> TextIdentifierMachine.acceptIdentifier(reader,
                                                                                           outputSequence,
                                                                                           UnaryExpressionOutputChain::setVariableName,
                                                                                           exceptionThrower))
                .setTemporary()
                .build();

        var postfixOperator = new State.Builder<UnaryExpressionOutputChain, SyntaxException>()
                .setName("POSTFIX OPERATOR")
                .setAcceptor(new OperatorAcceptor<>(operatorFactory,
                                                    UnaryExpressionOutputChain::setUnaryOperator))
                .setFinal()
                .build();

        return new PostfixUnaryOperatorMachine(
                TransitionMatrix.chainedTransitions(expression, postfixOperator), exceptionThrower);
    }
}
