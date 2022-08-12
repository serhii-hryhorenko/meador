package com.teamdev.meador.programelement.variable.fsmimpl;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.runtime.variable.VariableHolder;

/**
 * {@link FiniteStateMachine} implementation for recognizing variable declarations in Meador
 * programs.
 */
public class VariableDeclarationMachine extends FiniteStateMachine<VariableHolder, SyntaxException> {

    private VariableDeclarationMachine(
            TransitionMatrix<VariableHolder, SyntaxException> transitionMatrix) {
        super(transitionMatrix, new ExceptionThrower<>(() -> new SyntaxException("Failed to declare a variable.")));
    }

    public static VariableDeclarationMachine create(
            StateAcceptor<VariableHolder, SyntaxException> expressionAcceptor) {

        Preconditions.checkNotNull(expressionAcceptor);

        var variableName = new State.Builder<VariableHolder, SyntaxException>()
                .setName("VARIABLE NAME")
                .setAcceptor(
                        (reader, outputSequence) -> TextIdentifierMachine.acceptIdentifier(reader,
                                                                                           outputSequence,
                                                                                           VariableHolder::setName,
                                                                                           new ExceptionThrower<>(
                                                                                                   () -> new SyntaxException("Failed to parse a name for a variable."))))
                .build();

        var assignOperator = new State.Builder<VariableHolder, SyntaxException>()
                .setName("ASSIGN OPERATOR")
                .setAcceptor(StateAcceptor.acceptChar('='))
                .build();

        var expression = new State.Builder<VariableHolder, SyntaxException>()
                .setName("ASSIGNED VALUE")
                .setAcceptor(expressionAcceptor)
                .build();

        var semicolon = new State.Builder<VariableHolder, SyntaxException>()
                .setName("VARIABLE SEPARATOR")
                .setAcceptor(StateAcceptor.acceptChar(';'))
                .setFinal()
                .build();

        var matrix = TransitionMatrix.chainedTransitions(variableName, assignOperator, expression,
                                                         semicolon);

        return new VariableDeclarationMachine(matrix);
    }
}
