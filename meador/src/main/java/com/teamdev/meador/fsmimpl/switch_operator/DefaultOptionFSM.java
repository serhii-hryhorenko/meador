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

    private DefaultOptionFSM(TransitionMatrix<SwitchContext, CompilingException> transitionMatrix, ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }

    public static DefaultOptionFSM create(StatementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var defaultKeyword = new State.Builder<SwitchContext, CompilingException>()
                .setName("DEFAULT OPTION")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var optionalWord = TextIdentifierFSM.execute(inputSequence, new ExceptionThrower<>(CompilingException::new));
                    return optionalWord.isPresent() && optionalWord.get().equals("default");
                })
                .build();

        var colon = new State.Builder<SwitchContext, CompilingException>()
                .setName("COLON")
                .setAcceptor(StateAcceptor.acceptChar(':'))
                .build();

        var executableExpression = new State.Builder<SwitchContext, CompilingException>()
                .setName("EXECUTABLE EXPRESSION")
                .setAcceptor(CodeBlockFSM.create(factory, SwitchContext::setDefaultCommand))
                .setFinite(true)
                .build();

        var matrix =
                TransitionMatrix.chainedTransitions(defaultKeyword, colon, executableExpression);

        return new DefaultOptionFSM(matrix, new ExceptionThrower<>(CompilingException::new));
    }
}
