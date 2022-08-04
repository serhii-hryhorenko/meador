package com.teamdev.meador.fsmimpl.conditional_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.fsmimpl.util.CodeBlockMachine;
import com.teamdev.meador.fsmimpl.util.BracketedValueMachine;

import static com.teamdev.meador.compiler.ProgramElement.BOOLEAN_EXPRESSION;

/**
 * {@link FiniteStateMachine} implementation for parsing {@code if} operator.
 * Parsing result is stored at {@link IfOperatorOutputChain}.
 */
public class IfOperatorMachine extends FiniteStateMachine<IfOperatorOutputChain, CompilingException> {
    private static final String IF = "if";

    public static IfOperatorMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var ifKeyword = new State.Builder<IfOperatorOutputChain, CompilingException>()
                .setName("IF KEYWORD")
                .setAcceptor((reader, outputSequence) ->
                        TextIdentifierMachine.acceptKeyword(reader, IF, new ExceptionThrower<>(CompilingException::new)))
                .setTemporary()
                .build();

        var condition = new State.Builder<IfOperatorOutputChain, CompilingException>()
                .setName("IF CONDITION")
                .setAcceptor(BracketedValueMachine.create(factory, BOOLEAN_EXPRESSION, IfOperatorOutputChain::setCondition))
                .build();

        var ifCodeBlock = new State.Builder<IfOperatorOutputChain, CompilingException>()
                .setName("IF CODE BLOCK")
                .setAcceptor(CodeBlockMachine.create(factory, IfOperatorOutputChain::setStatements))
                .setFinal()
                .build();

        var matrix = TransitionMatrix.chainedTransitions(ifKeyword, condition, ifCodeBlock);

        return new IfOperatorMachine(matrix, new ExceptionThrower<>(CompilingException::new));
    }

    private IfOperatorMachine(TransitionMatrix<IfOperatorOutputChain, CompilingException> transitionMatrix,
                              ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
