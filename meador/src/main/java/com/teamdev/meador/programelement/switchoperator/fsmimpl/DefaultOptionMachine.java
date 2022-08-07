package com.teamdev.meador.programelement.switchoperator.fsmimpl;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.programelement.CompilingException;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.util.CodeBlockMachine;

/**
 * {@link FiniteStateMachine} implementation for parsing {@code default: { code; }} structure.
 */
public class DefaultOptionMachine extends FiniteStateMachine<SwitchOperatorOutputChain, CompilingException> {

    private static final String DEFAULT = "default";

    private DefaultOptionMachine(
            TransitionMatrix<SwitchOperatorOutputChain, CompilingException> transitionMatrix,
            ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }

    public static DefaultOptionMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var exceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var defaultKeyword = new State.Builder<SwitchOperatorOutputChain, CompilingException>()
                .setName("DEFAULT OPTION")
                .setAcceptor((inputSequence, outputSequence) ->
                                     TextIdentifierMachine.acceptKeyword(inputSequence, DEFAULT,
                                                                         exceptionThrower))
                .build();

        var colon = new State.Builder<SwitchOperatorOutputChain, CompilingException>()
                .setName("COLON")
                .setAcceptor(StateAcceptor.acceptChar(':'))
                .build();

        var executableExpression = new State.Builder<SwitchOperatorOutputChain, CompilingException>()
                .setName("EXECUTABLE EXPRESSION")
                .setAcceptor(CodeBlockMachine.create(factory,
                                                     SwitchOperatorOutputChain::setDefaultCommand))
                .setFinal()
                .build();

        var matrix =
                TransitionMatrix.chainedTransitions(defaultKeyword, colon, executableExpression);

        return new DefaultOptionMachine(matrix, exceptionThrower);
    }
}
