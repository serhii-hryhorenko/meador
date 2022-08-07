package com.teamdev.meador.compiler.whileloop.fsmimpl;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.compiler.util.CodeBlockMachine;
import com.teamdev.meador.compiler.util.CompileStatementAcceptor;

import static com.teamdev.meador.compiler.ProgramElement.BOOLEAN_EXPRESSION;

public class WhileLoopMachine extends FiniteStateMachine<WhileLoopOutputChain, CompilingException> {

    private static final String WHILE = "while";

    private WhileLoopMachine(
            TransitionMatrix<WhileLoopOutputChain, CompilingException> transitionMatrix,
            ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }

    public static WhileLoopMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var exceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var whileKeyword = new State.Builder<WhileLoopOutputChain, CompilingException>()
                .setName("WHILE KEYWORD")
                .setAcceptor(
                        (reader, outputChain) -> TextIdentifierMachine.acceptKeyword(reader, WHILE,
                                                                                     exceptionThrower))
                .setTemporary()
                .build();

        var condition = new State.Builder<WhileLoopOutputChain, CompilingException>()
                .setName("LOOP CONDITION")
                .setAcceptor(new CompileStatementAcceptor<>(factory, BOOLEAN_EXPRESSION,
                                                            WhileLoopOutputChain::setCondition))
                .build();

        var loopBody = new State.Builder<WhileLoopOutputChain, CompilingException>()
                .setName("WHILE LOOP BODY")
                .setAcceptor(CodeBlockMachine.create(factory,
                                                     WhileLoopOutputChain::setLoopBodyStatements))
                .setFinal()
                .build();

        return new WhileLoopMachine(
                TransitionMatrix.chainedTransitions(whileKeyword, condition, loopBody),
                exceptionThrower);
    }
}
