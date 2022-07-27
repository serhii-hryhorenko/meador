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

public class PrefixOperatorFSM extends FiniteStateMachine<UnaryExpressionOutputChain, CompilingException> {

    public static PrefixOperatorFSM create(StatementCompilerFactory compilerFactory,
                                           AbstractOperatorFactory<AbstractUnaryOperator> operatorFactory) {
        Preconditions.checkNotNull(compilerFactory, operatorFactory);

        var exceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var prefixOperator = new State.Builder<UnaryExpressionOutputChain, CompilingException>()
                .setName("PREFIX OPERATOR")
                .setAcceptor(new OperatorAcceptor<>(operatorFactory, UnaryExpressionOutputChain::setUnaryOperator))
                .setTemporary(true)
                .build();


        var expression = new State.Builder<UnaryExpressionOutputChain, CompilingException>()
                .setName("VARIABLE NAME")
                .setAcceptor((reader, outputSequence) -> TextIdentifierFSM.acceptIdentifier(reader, outputSequence,
                        UnaryExpressionOutputChain::setVariableName,
                        exceptionThrower))
                .setFinite(true)
                .build();

        return new PrefixOperatorFSM(TransitionMatrix.chainedTransitions(prefixOperator, expression),
                exceptionThrower);
    }

    private PrefixOperatorFSM(TransitionMatrix<UnaryExpressionOutputChain, CompilingException> transitionMatrix,
                              ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
