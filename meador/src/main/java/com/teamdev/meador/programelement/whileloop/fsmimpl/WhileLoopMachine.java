package com.teamdev.meador.programelement.whileloop.fsmimpl;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.util.CodeBlockMachine;
import com.teamdev.meador.programelement.util.CompileStatementAcceptor;

import static com.teamdev.meador.programelement.ProgramElement.BOOLEAN_EXPRESSION;

public class WhileLoopMachine extends FiniteStateMachine<WhileLoopOutputChain, SyntaxException> {

    private static final String WHILE = "while";

    private WhileLoopMachine(
            TransitionMatrix<WhileLoopOutputChain, SyntaxException> transitionMatrix,
            ExceptionThrower<SyntaxException> thrower) {
        super(transitionMatrix, thrower);
    }

    public static WhileLoopMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var exceptionThrower = new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize while loop operator."));

        var whileKeyword = new State.Builder<WhileLoopOutputChain, SyntaxException>()
                .setName("WHILE KEYWORD")
                .setAcceptor(
                        (reader, outputChain) -> TextIdentifierMachine.acceptKeyword(reader, WHILE,
                                                                                     exceptionThrower))
                .setTemporary()
                .build();

        var condition = new State.Builder<WhileLoopOutputChain, SyntaxException>()
                .setName("LOOP CONDITION")
                .setAcceptor(new CompileStatementAcceptor<>(factory, BOOLEAN_EXPRESSION,
                                                            WhileLoopOutputChain::setCondition))
                .build();

        var loopBody = new State.Builder<WhileLoopOutputChain, SyntaxException>()
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
