package com.teamdev.meador.fsmimpl.switch_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.fsmimpl.util.CodeBlockMachine;

import static com.teamdev.meador.compiler.ProgramElement.EXPRESSION;

/**
 * {@link FiniteStateMachine} implementation for parsing {@code case: value { code; }} structure.
 */
public class CaseOptionMachine extends FiniteStateMachine<CaseOptionOutputChain, CompilingException> {

    private static final String CASE = "case";

    private CaseOptionMachine(
            TransitionMatrix<CaseOptionOutputChain, CompilingException> transitionMatrix,
            ExceptionThrower<CompilingException> exceptionThrower) {
        super(transitionMatrix, exceptionThrower);
    }

    public static CaseOptionMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var exceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var keyword = new State.Builder<CaseOptionOutputChain, CompilingException>()
                .setName("CASE KEYWORD")
                .setAcceptor((inputSequence, outputSequence) ->
                                     TextIdentifierMachine.acceptKeyword(inputSequence, CASE,
                                                                         exceptionThrower))
                .build();

        var statement = new State.Builder<CaseOptionOutputChain, CompilingException>()
                .setName("CASE VALUE")
                .setAcceptor(new CompileStatementAcceptor<>(factory, EXPRESSION,
                                                            CaseOptionOutputChain::setCondition))
                .build();

        var colon = new State.Builder<CaseOptionOutputChain, CompilingException>()
                .setName("COLON")
                .setAcceptor(StateAcceptor.acceptChar(':'))
                .build();

        var executableExpression = new State.Builder<CaseOptionOutputChain, CompilingException>()
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
