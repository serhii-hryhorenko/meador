package com.teamdev.machine.expression;

import com.google.common.base.Preconditions;
import com.teamdev.machine.operand.OperandFSM;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.fsm.TransitionMatrixBuilder;
import com.teamdev.math.bioperator.AbstractBinaryOperator;
import com.teamdev.math.bioperator.AbstractBinaryOperatorFactory;

import java.util.function.BiConsumer;

/**
 * {@link FiniteStateMachine} implementation for recognizing the structure of math expression that
 * consist of operands and binary operators.
 * Operand may be a number, an expression in brackets, or a function —
 * see {@link OperandFSM} for details.
 */
public class ExpressionFSM<O, E extends Exception> extends FiniteStateMachine<O, E> {

    private ExpressionFSM(TransitionMatrix<O, E> transitionMatrix,
                          ExceptionThrower<E> thrower) {
        super(transitionMatrix, thrower);
    }

    public static <O, E extends Exception> ExpressionFSM<O, E> create(
            StateAcceptor<O, E> operandAcceptor,
            AbstractBinaryOperatorFactory factory,
            BiConsumer<O, AbstractBinaryOperator> operatorConsumer,
            ExceptionThrower<E> thrower) {

        Preconditions.checkNotNull(operandAcceptor, operatorConsumer);

        var initialState = State.<O, E>initialState();

        var operandState = new State.Builder<O, E>()
                .setName("OPERAND")
                .setAcceptor(Preconditions.checkNotNull(operandAcceptor))
                .setFinite(true)
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
}
