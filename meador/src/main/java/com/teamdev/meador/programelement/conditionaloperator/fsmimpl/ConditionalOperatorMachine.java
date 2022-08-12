package com.teamdev.meador.programelement.conditionaloperator.fsmimpl;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.fsm.TransitionMatrixBuilder;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.util.CodeBlockMachine;

/**
 * {@link FiniteStateMachine} implementation for parsing Meador conditional operators.
 * Parsing result is stored at {@link ConditionalOperatorOutputChain}.
 */
public class ConditionalOperatorMachine extends FiniteStateMachine<ConditionalOperatorOutputChain, SyntaxException> {

    private static final String ELSE = "else";

    private ConditionalOperatorMachine(
            TransitionMatrix<ConditionalOperatorOutputChain, SyntaxException> transitionMatrix,
            ExceptionThrower<SyntaxException> thrower) {
        super(transitionMatrix, thrower);
    }

    public static ConditionalOperatorMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var exceptionThrower = new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize conditional operator."));

        var initial = State.<ConditionalOperatorOutputChain, SyntaxException>initialState();

        var ifOperator = new State.Builder<ConditionalOperatorOutputChain, SyntaxException>()
                .setName("IF OPERATOR")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var context = new IfOperatorOutputChain();

                    if (IfOperatorMachine.create(factory)
                            .accept(inputSequence, context)) {
                        outputSequence.addConditionalOperator(context);
                        return true;
                    }

                    return false;
                })
                .setFinal()
                .build();

        var elseKeyword = new State.Builder<ConditionalOperatorOutputChain, SyntaxException>()
                .setName("ELSE KEYWORD")
                .setAcceptor((reader, outputSequence) -> TextIdentifierMachine.acceptKeyword(reader,
                                                                                             ELSE,
                                                                                             exceptionThrower))
                .setTemporary()
                .build();

        var elseCodeBlock = new State.Builder<ConditionalOperatorOutputChain, SyntaxException>()
                .setName("ELSE CODE BLOCK")
                .setAcceptor(CodeBlockMachine.create(factory,
                                                     ConditionalOperatorOutputChain::setElseStatementList))
                .setFinal()
                .build();

        var matrix = new TransitionMatrixBuilder<ConditionalOperatorOutputChain, SyntaxException>()
                .withStartState(initial)
                .allowTransition(initial, ifOperator)
                .allowTransition(ifOperator, elseKeyword)
                .allowTransition(elseKeyword, elseCodeBlock, ifOperator)
                .build();

        return new ConditionalOperatorMachine(matrix, exceptionThrower);
    }
}
