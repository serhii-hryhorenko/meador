package com.teamdev.meador.fsmimpl.while_loop;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;

import static com.teamdev.meador.compiler.ProgramElement.BOOLEAN_EXPRESSION;
import static com.teamdev.meador.compiler.ProgramElement.CODE_BLOCK;

public class WhileLoopMachine extends FiniteStateMachine<WhileLoopOutputChain, CompilingException> {

    private static final String WHILE = "while";

    public static WhileLoopMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var exceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var whileKeyword = new State.Builder<WhileLoopOutputChain, CompilingException>()
                .setName("WHILE KEYWORD")
                .setAcceptor((reader, outputChain) -> TextIdentifierMachine.acceptKeyword(reader, WHILE, exceptionThrower))
                .setTemporary()
                .build();

        var condition = new State.Builder<WhileLoopOutputChain, CompilingException>()
                .setName("LOOP CONDITION")
                .setAcceptor(new CompileStatementAcceptor<>(factory, BOOLEAN_EXPRESSION, WhileLoopOutputChain::setCondition))
                .build();

        var loopBody = new State.Builder<WhileLoopOutputChain, CompilingException>()
                .setName("WHILE LOOP BODY")
                .setAcceptor(new CompileStatementAcceptor<>(factory, CODE_BLOCK, WhileLoopOutputChain::setLoopBodyStatements))
                .setFinal()
                .build();

        return new WhileLoopMachine(TransitionMatrix.chainedTransitions(whileKeyword, condition, loopBody), exceptionThrower);
    }

    private WhileLoopMachine(TransitionMatrix<WhileLoopOutputChain, CompilingException> transitionMatrix,
                             ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
