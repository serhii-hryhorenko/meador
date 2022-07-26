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

import static com.teamdev.runtime.value.operator.unaryoperator.Position.PREFIX;

public class PrefixOperatorFSM extends FiniteStateMachine<UnaryExpressionOutputChain, CompilingException> {

    public static PrefixOperatorFSM create(StatementCompilerFactory compilerFactory, AbstractUnaryOperatorFactory operatorFactory) {
        Preconditions.checkNotNull(compilerFactory, operatorFactory);

        var prefixOperator = new State.Builder<UnaryExpressionOutputChain, CompilingException>()
                .setName("PREFIX OPERATOR")
                .setAcceptor(new UnaryOperatorAcceptor<>(operatorFactory, UnaryExpressionOutputChain::setUnaryOperator, PREFIX))
                .setTemporary(true)
                .build();

        var expression = new State.Builder<UnaryExpressionOutputChain, CompilingException>()
                .setName("VARIABLE NAME")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var optionalName = TextIdentifierFSM.execute(inputSequence,
                            new ExceptionThrower<>(CompilingException::new));

                    optionalName.ifPresent(outputSequence::setVariableName);
                    return optionalName.isPresent();
                })
                .setFinite(true)
                .build();

        return new PrefixOperatorFSM(TransitionMatrix.chainedTransitions(prefixOperator, expression),
                new ExceptionThrower<>(CompilingException::new));
    }

    private PrefixOperatorFSM(TransitionMatrix<UnaryExpressionOutputChain, CompilingException> transitionMatrix,
                              ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
