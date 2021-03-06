package com.teamdev.meador.fsmimpl.switch_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.compiler.StatementType;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchOptionContext;
import com.teamdev.meador.fsmimpl.util.CodeBlockFSM;

/**
 * {@link FiniteStateMachine} implementation for parsing {@code case: value { code; }} structure.
 */
public class CaseOptionFSM extends FiniteStateMachine<SwitchOptionContext, CompilingException> {

    private static final String CASE = "case";

    public static CaseOptionFSM create(StatementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var exceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var keyword = new State.Builder<SwitchOptionContext, CompilingException>()
                .setName("CASE KEYWORD")
                .setAcceptor((inputSequence, outputSequence) ->
                        TextIdentifierFSM.acceptKeyword(inputSequence, CASE, exceptionThrower))
                .build();

        var statement = new State.Builder<SwitchOptionContext, CompilingException>()
                .setName("CASE VALUE")
                .setAcceptor(new CompileStatementAcceptor<>(factory, StatementType.EXPRESSION, SwitchOptionContext::setCondition))
                .build();

        var colon = new State.Builder<SwitchOptionContext, CompilingException>()
                .setName("COLON")
                .setAcceptor(StateAcceptor.acceptChar(':'))
                .build();

        var executableExpression = new State.Builder<SwitchOptionContext, CompilingException>()
                .setName("EXPRESSION BLOCK")
                .setAcceptor(CodeBlockFSM.create(factory, SwitchOptionContext::setStatement))
                .setFinite(true)
                .build();

        var matrix =
                TransitionMatrix.chainedTransitions(keyword, statement, colon, executableExpression);


        return new CaseOptionFSM(matrix, exceptionThrower);
    }

    private CaseOptionFSM(TransitionMatrix<SwitchOptionContext, CompilingException> transitionMatrix,
                          ExceptionThrower<CompilingException> exceptionThrower) {
        super(transitionMatrix, exceptionThrower);
    }
}
