package com.teamdev.meador.fsmimpl.switch_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchContext;
import com.teamdev.meador.fsmimpl.util.CodeBlockFSM;

/**
 * {@link FiniteStateMachine} implementation for parsing {@code default: { code; }} structure.
 */
public class DefaultOptionFSM extends FiniteStateMachine<SwitchContext, CompilingException> {

    private static final String DEFAULT = "default";

    private DefaultOptionFSM(TransitionMatrix<SwitchContext, CompilingException> transitionMatrix, ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }

    public static DefaultOptionFSM create(StatementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var exceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var defaultKeyword = new State.Builder<SwitchContext, CompilingException>()
                .setName("DEFAULT OPTION")
                .setAcceptor((inputSequence, outputSequence) ->
                        TextIdentifierFSM.acceptKeyword(inputSequence, DEFAULT, exceptionThrower))
                .build();

        var colon = new State.Builder<SwitchContext, CompilingException>()
                .setName("COLON")
                .setAcceptor(StateAcceptor.acceptChar(':'))
                .build();

        var executableExpression = new State.Builder<SwitchContext, CompilingException>()
                .setName("EXECUTABLE EXPRESSION")
                .setAcceptor(CodeBlockFSM.create(factory, SwitchContext::setDefaultCommand))
                .setFinal()
                .build();

        var matrix =
                TransitionMatrix.chainedTransitions(defaultKeyword, colon, executableExpression);

        return new DefaultOptionFSM(matrix, exceptionThrower);
    }
}
