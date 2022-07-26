package com.teamdev.meador.fsmimpl.unary_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.runtime.value.operator.unaryoperator.AbstractUnaryOperatorFactory;

import static com.teamdev.runtime.value.operator.unaryoperator.Position.POSTFIX;

public class PostfixOperatorFSM extends FiniteStateMachine<UnaryExpressionOutputChain, CompilingException> {

    public static PostfixOperatorFSM create(StatementCompilerFactory compilerFactory, AbstractUnaryOperatorFactory operatorFactory) {
        Preconditions.checkNotNull(compilerFactory, operatorFactory);

        var expression = new State.Builder<UnaryExpressionOutputChain, CompilingException>()
                .setName("VARIABLE NAME")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var optionalName = TextIdentifierFSM.execute(inputSequence,
                            new ExceptionThrower<>(CompilingException::new));

                    optionalName.ifPresent(outputSequence::setVariableName);
                    return optionalName.isPresent();
                })
                .setTemporary(true)
                .build();

        var postfixOperator = new State.Builder<UnaryExpressionOutputChain, CompilingException>()
                .setName("POSTFIX OPERATOR")
                .setAcceptor(new UnaryOperatorAcceptor<>(operatorFactory, UnaryExpressionOutputChain::setUnaryOperator, POSTFIX))
                .setFinite(true)
                .build();

        return new PostfixOperatorFSM(TransitionMatrix.chainedTransitions(expression, postfixOperator),
                new ExceptionThrower<>(CompilingException::new));
    }

    private PostfixOperatorFSM(TransitionMatrix<UnaryExpressionOutputChain, CompilingException> transitionMatrix,
                               ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
