package com.teamdev.calculator_api;

import com.google.common.base.Preconditions;
import com.teamdev.calculator_api.resolver.MathElementResolverFactory;
import com.teamdev.calculator_api.resolver.ResolveMathElementAcceptor;
import com.teamdev.calculator_api.resolver.ResolvingException;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.fsm.State;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.runtime.ShuntingYard;

import static com.teamdev.calculator_api.resolver.MathElement.EXPRESSION;

/**
 * {@link  FiniteStateMachine} implementation used as an entry point for starting evaluation.
 * Ensures that {@link InputSequenceReader} is fully interpreted.
 */
class CalculatorMachine extends FiniteStateMachine<ShuntingYard, ResolvingException> {

    private CalculatorMachine(TransitionMatrix<ShuntingYard, ResolvingException> transitionMatrix,
                              ExceptionThrower<ResolvingException> thrower) {
        super(transitionMatrix, thrower);
    }

    static CalculatorMachine create(MathElementResolverFactory factory) {
        Preconditions.checkNotNull(factory);

        var expressionState = new State.Builder<ShuntingYard, ResolvingException>()
                .setName("EXPRESSION")
                .setAcceptor(new ResolveMathElementAcceptor<>(factory, EXPRESSION,
                                                              ShuntingYard::pushOperand))
                .build();

        var finalState = new State.Builder<ShuntingYard, ResolvingException>()
                .setName("FINAL")
                .setAcceptor(((inputSequence, outputSequence) -> !inputSequence.canRead()))
                .setFinal()
                .build();

        var matrix = TransitionMatrix.chainedTransitions(
                expressionState, finalState
        );

        return new CalculatorMachine(matrix, new ExceptionThrower<>(ResolvingException::new));
    }
}
