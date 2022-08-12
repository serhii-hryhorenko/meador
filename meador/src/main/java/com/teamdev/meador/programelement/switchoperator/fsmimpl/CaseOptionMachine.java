package com.teamdev.meador.programelement.switchoperator.fsmimpl;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.util.CodeBlockMachine;
import com.teamdev.meador.programelement.util.CompileStatementAcceptor;

import static com.teamdev.meador.programelement.ProgramElement.EXPRESSION;

/**
 * {@link FiniteStateMachine} implementation for parsing {@code case: value { code; }} structure.
 */
public class CaseOptionMachine extends FiniteStateMachine<CaseOptionOutputChain, SyntaxException> {

    private static final String CASE = "case";

    private CaseOptionMachine(
            TransitionMatrix<CaseOptionOutputChain, SyntaxException> transitionMatrix,
            ExceptionThrower<SyntaxException> exceptionThrower) {
        super(transitionMatrix, exceptionThrower);
    }

    public static CaseOptionMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var exceptionThrower = new ExceptionThrower<>(() -> new SyntaxException("Failed to parse case option of switch operator."));

        var keyword = new State.Builder<CaseOptionOutputChain, SyntaxException>()
                .setName("CASE KEYWORD")
                .setAcceptor((inputSequence, outputSequence) ->
                                     TextIdentifierMachine.acceptKeyword(inputSequence, CASE,
                                                                         exceptionThrower))
                .build();

        var statement = new State.Builder<CaseOptionOutputChain, SyntaxException>()
                .setName("CASE VALUE")
                .setAcceptor(new CompileStatementAcceptor<>(factory, EXPRESSION,
                                                            CaseOptionOutputChain::setCondition))
                .build();

        var colon = new State.Builder<CaseOptionOutputChain, SyntaxException>()
                .setName("COLON")
                .setAcceptor(StateAcceptor.acceptChar(':'))
                .build();

        var executableExpression = new State.Builder<CaseOptionOutputChain, SyntaxException>()
                .setName("EXPRESSION BLOCK")
                .setAcceptor(CodeBlockMachine.create(factory, CaseOptionOutputChain::setStatement))
                .setFinal()
                .build();

        var matrix =
                TransitionMatrix.chainedTransitions(keyword, statement, colon,
                                                    executableExpression);

        return new CaseOptionMachine(matrix, exceptionThrower);
    }
}
