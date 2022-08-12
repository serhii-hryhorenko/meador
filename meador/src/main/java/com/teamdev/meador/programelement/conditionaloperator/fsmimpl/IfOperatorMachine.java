package com.teamdev.meador.programelement.conditionaloperator.fsmimpl;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.util.BracketedValueMachine;
import com.teamdev.meador.programelement.util.CodeBlockMachine;

import static com.teamdev.meador.programelement.ProgramElement.BOOLEAN_EXPRESSION;

/**
 * {@link FiniteStateMachine} implementation for parsing {@code if} operator.
 * Parsing result is stored at {@link IfOperatorOutputChain}.
 */
public class IfOperatorMachine extends FiniteStateMachine<IfOperatorOutputChain, SyntaxException> {

    private static final String IF = "if";

    private IfOperatorMachine(
            TransitionMatrix<IfOperatorOutputChain, SyntaxException> transitionMatrix,
            ExceptionThrower<SyntaxException> thrower) {
        super(transitionMatrix, thrower);
    }

    public static IfOperatorMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var exceptionThrower = new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize if operator."));

        var ifKeyword = new State.Builder<IfOperatorOutputChain, SyntaxException>()
                .setName("IF KEYWORD")
                .setAcceptor(
                        (reader, outputSequence) -> TextIdentifierMachine.acceptKeyword(reader, IF, exceptionThrower))
                .setTemporary()
                .build();

        var condition = new State.Builder<IfOperatorOutputChain, SyntaxException>()
                .setName("IF CONDITION")
                .setAcceptor(BracketedValueMachine.create(factory, BOOLEAN_EXPRESSION,
                                                          IfOperatorOutputChain::setCondition))
                .build();

        var ifCodeBlock = new State.Builder<IfOperatorOutputChain, SyntaxException>()
                .setName("IF CODE BLOCK")
                .setAcceptor(CodeBlockMachine.create(factory, IfOperatorOutputChain::setStatements))
                .setFinal()
                .build();

        var matrix = TransitionMatrix.chainedTransitions(ifKeyword, condition, ifCodeBlock);

        return new IfOperatorMachine(matrix, exceptionThrower);
    }
}
