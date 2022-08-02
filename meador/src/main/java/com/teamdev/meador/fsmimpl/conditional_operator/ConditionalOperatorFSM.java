package com.teamdev.meador.fsmimpl.conditional_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.fsmimpl.util.CodeBlockFSM;

public class ConditionalOperatorFSM extends FiniteStateMachine<ConditionalOperatorContext, CompilingException> {

    private static final String ELSE = "else";

    public static ConditionalOperatorFSM create(StatementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var initial = State.<ConditionalOperatorContext, CompilingException>initialState();

        var ifOperator = new State.Builder<ConditionalOperatorContext, CompilingException>()
                .setName("IF OPERATOR")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var context = new IfOperatorContext();

                    if (IfOperatorFSM.create(factory).accept(inputSequence, context)) {
                        outputSequence.addIfOperator(context);
                        return true;
                    }

                    return false;
                })
                .setFinal()
                .build();

        var elseKeyword = new State.Builder<ConditionalOperatorContext, CompilingException>()
                .setName("ELSE KEYWORD")
                .setAcceptor((reader, outputSequence) ->
                        TextIdentifierFSM.acceptKeyword(reader, ELSE, new ExceptionThrower<>(CompilingException::new)))
                .setTemporary()
                .build();

        var elseCodeBlock = new State.Builder<ConditionalOperatorContext, CompilingException>()
                .setName("ELSE CODE BLOCK")
                .setAcceptor(CodeBlockFSM.create(factory, ConditionalOperatorContext::setElseStatements))
                .setFinal()
                .build();

        var matrix = new TransitionMatrixBuilder<ConditionalOperatorContext, CompilingException>()
                .withStartState(initial)
                .allowTransition(initial, ifOperator)
                .allowTransition(ifOperator, elseKeyword)
                .allowTransition(elseKeyword, elseCodeBlock, ifOperator)
                .build();

        return new ConditionalOperatorFSM(matrix, new ExceptionThrower<>(CompilingException::new));
    }

    private ConditionalOperatorFSM(TransitionMatrix<ConditionalOperatorContext, CompilingException> transitionMatrix,
                                   ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
