package com.teamdev.calculation.machine.calculator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.InputSequence;
import com.teamdev.fsm.State;
import com.teamdev.fsm.TransitionMatrix;

/**
 * {@link  FiniteStateMachine} implementation used as an entry point for starting evaluation.
 * Ensures that {@link InputSequence} is fully interpreted.
 */
public class CalculatorFSM extends FiniteStateMachine<ShuntingYard, ResolvingException> {

    private CalculatorFSM(TransitionMatrix<ShuntingYard, ResolvingException> transitionMatrix,
                          ExceptionThrower<ResolvingException> thrower) {
        super(transitionMatrix, thrower);
    }

    public static CalculatorFSM create(MathElementResolverFactory factory) {
        Preconditions.checkNotNull(factory);

        var expressionState = new State.Builder<ShuntingYard, ResolvingException>()
                .setName("EXPRESSION")
                .setAcceptor(new ResolveMathElementAcceptor<>(factory, MathElement.EXPRESSION,
                                                              ShuntingYard::pushOperand))
                .build();

        var finalState = new State.Builder<ShuntingYard, ResolvingException>()
                .setName("FINAL")
                .setAcceptor(((inputSequence, outputSequence) -> !inputSequence.canRead()))
                .setFinite(true)
                .build();

        var matrix = TransitionMatrix.chainedTransitions(
                expressionState, finalState
        );

        return new CalculatorFSM(matrix, new ExceptionThrower<>(ResolvingException::new));
    }
}
