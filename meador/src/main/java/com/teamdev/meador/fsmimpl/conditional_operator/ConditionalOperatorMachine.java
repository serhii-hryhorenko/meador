package com.teamdev.meador.fsmimpl.conditional_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.fsmimpl.util.CodeBlockMachine;

/**
 * {@link FiniteStateMachine} implementation for parsing Meador conditional operators.
 * Parsing result is stored at {@link ConditionalOperatorOutputChain}.
 */
public class ConditionalOperatorMachine extends FiniteStateMachine<ConditionalOperatorOutputChain, CompilingException> {
    private static final String ELSE = "else";

    public static ConditionalOperatorMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var exceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var initial = State.<ConditionalOperatorOutputChain, CompilingException>initialState();

        var ifOperator = new State.Builder<ConditionalOperatorOutputChain, CompilingException>()
                .setName("IF OPERATOR")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var context = new IfOperatorOutputChain();

                    if (IfOperatorMachine.create(factory).accept(inputSequence, context)) {
                        outputSequence.addConditionalOperator(context);
                        return true;
                    }

                    return false;
                })
                .setFinal()
                .build();

        var elseKeyword = new State.Builder<ConditionalOperatorOutputChain, CompilingException>()
                .setName("ELSE KEYWORD")
                .setAcceptor((reader, outputSequence) ->
                        TextIdentifierMachine.acceptKeyword(reader, ELSE, exceptionThrower))
                .setTemporary()
                .build();

        var elseCodeBlock = new State.Builder<ConditionalOperatorOutputChain, CompilingException>()
                .setName("ELSE CODE BLOCK")
                .setAcceptor(CodeBlockMachine.create(factory, ConditionalOperatorOutputChain::setElseStatementList))
                .setFinal()
                .build();

        var matrix = new TransitionMatrixBuilder<ConditionalOperatorOutputChain, CompilingException>()
                .withStartState(initial)
                .allowTransition(initial, ifOperator)
                .allowTransition(ifOperator, elseKeyword)
                .allowTransition(elseKeyword, elseCodeBlock, ifOperator)
                .build();

        return new ConditionalOperatorMachine(matrix, exceptionThrower);
    }

    private ConditionalOperatorMachine(TransitionMatrix<ConditionalOperatorOutputChain, CompilingException> transitionMatrix,
                                       ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
