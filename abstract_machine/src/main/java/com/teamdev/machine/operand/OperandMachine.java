package com.teamdev.machine.operand;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.TransitionMatrix;

/**
 * {@link FiniteStateMachine} implementation with "one of" logic for recognizing implemented
 * operands
 * from any supported math expression.
 */

public class OperandMachine<O, E extends Exception> extends FiniteStateMachine<O, E> {

    private OperandMachine(TransitionMatrix<O, E> transitionMatrix,
                           ExceptionThrower<E> exceptionThrower) {

        super(transitionMatrix, exceptionThrower);
    }

    public static <O, E extends Exception> OperandMachine<O, E> create(TransitionMatrix<O, E> oneOfMatrix,
                                                                       ExceptionThrower<E> exceptionThrower) {
        Preconditions.checkNotNull(oneOfMatrix, exceptionThrower);
        return new OperandMachine<>(oneOfMatrix, exceptionThrower);
    }
}
