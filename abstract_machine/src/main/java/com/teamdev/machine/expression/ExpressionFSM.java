package com.teamdev.machine.expression;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.operand.OperandFSM;
import com.teamdev.runtime.value.bioperator.AbstractBinaryOperator;
import com.teamdev.runtime.value.bioperator.AbstractBinaryOperatorFactory;

import java.util.function.BiConsumer;

/**
 * {@link FiniteStateMachine} implementation for recognizing the structure of math expression that
 * consist of operands and binary operators.
 * Operand may be a number, an expression in brackets, or a function —
 * see {@link OperandFSM} for details.
 */
public class ExpressionFSM<O, E extends Exception> extends FiniteStateMachine<O, E> {

    public static <O, E extends Exception> ExpressionFSM<O, E> create(
            StateAcceptor<O, E> operandAcceptor,
            AbstractBinaryOperatorFactory factory,
            BiConsumer<O, AbstractBinaryOperator> operatorConsumer,
            ExceptionThrower<E> thrower) {

        Preconditions.checkNotNull(operandAcceptor, operatorConsumer);

        var initialState = State.<O, E>initialState();

        var operandState = new State.Builder<O, E>()
                .setAcceptor(Preconditions.checkNotNull(operandAcceptor))
                .setFinal()
                .build();

        var binaryOperatorState = new State.Builder<O, E>()
                .setName("BINARY OPERATOR")
                .setAcceptor(new BinaryOperatorAcceptor<>(Preconditions.checkNotNull(factory), operatorConsumer))
                .build();

        var matrix = new TransitionMatrixBuilder<O, E>()
                .withStartState(initialState)
                .allowTransition(initialState, operandState)
                .allowTransition(operandState, binaryOperatorState)
                .allowTransition(binaryOperatorState, operandState)
                .build();

        return new ExpressionFSM<>(matrix, thrower);
    }

    private ExpressionFSM(TransitionMatrix<O, E> transitionMatrix,
                          ExceptionThrower<E> thrower) {
        super(transitionMatrix, thrower);
    }
}
