package com.teamdev.meador.fsmimpl.switch_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.compiler.ProgramElement;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchOptionContext;
import com.teamdev.meador.fsmimpl.util.CodeBlockMachine;

/**
 * {@link FiniteStateMachine} implementation for parsing {@code case: value { code; }} structure.
 */
public class CaseOptionMachine extends FiniteStateMachine<SwitchOptionContext, CompilingException> {

    private static final String CASE = "case";

    public static CaseOptionMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var exceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var keyword = new State.Builder<SwitchOptionContext, CompilingException>()
                .setName("CASE KEYWORD")
                .setAcceptor((inputSequence, outputSequence) ->
                        TextIdentifierMachine.acceptKeyword(inputSequence, CASE, exceptionThrower))
                .build();

        var statement = new State.Builder<SwitchOptionContext, CompilingException>()
                .setName("CASE VALUE")
                .setAcceptor(new CompileStatementAcceptor<>(factory, ProgramElement.EXPRESSION, SwitchOptionContext::setCondition))
                .build();

        var colon = new State.Builder<SwitchOptionContext, CompilingException>()
                .setName("COLON")
                .setAcceptor(StateAcceptor.acceptChar(':'))
                .build();

        var executableExpression = new State.Builder<SwitchOptionContext, CompilingException>()
                .setName("EXPRESSION BLOCK")
                .setAcceptor(CodeBlockMachine.create(factory, SwitchOptionContext::setStatement))
                .setFinal()
                .build();

        var matrix =
                TransitionMatrix.chainedTransitions(keyword, statement, colon, executableExpression);


        return new CaseOptionMachine(matrix, exceptionThrower);
    }

    private CaseOptionMachine(TransitionMatrix<SwitchOptionContext, CompilingException> transitionMatrix,
                              ExceptionThrower<CompilingException> exceptionThrower) {
        super(transitionMatrix, exceptionThrower);
    }
}
