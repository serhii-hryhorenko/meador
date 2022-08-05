package com.teamdev.meador.fsmimpl.variable;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.runtime.variable.VariableHolder;

/**
 * {@link FiniteStateMachine} implementation for recognizing variable declarations in Meador
 * programs.
 */
public class VariableDeclarationMachine extends FiniteStateMachine<VariableHolder, CompilingException> {

    private VariableDeclarationMachine(
            TransitionMatrix<VariableHolder, CompilingException> transitionMatrix) {
        super(transitionMatrix, new ExceptionThrower<>(CompilingException::new));
    }

    public static VariableDeclarationMachine create(
            StateAcceptor<VariableHolder, CompilingException> expressionAcceptor) {

        Preconditions.checkNotNull(expressionAcceptor);

        var variableName = new State.Builder<VariableHolder, CompilingException>()
                .setName("VARIABLE NAME")
                .setAcceptor(
                        (reader, outputSequence) -> TextIdentifierMachine.acceptIdentifier(reader,
                                                                                           outputSequence,
                                                                                           VariableHolder::setName,
                                                                                           new ExceptionThrower<>(
                                                                                                   CompilingException::new)))
                .build();

        var assignOperator = new State.Builder<VariableHolder, CompilingException>()
                .setName("ASSIGN OPERATOR")
                .setAcceptor(StateAcceptor.acceptChar('='))
                .build();

        var expression = new State.Builder<VariableHolder, CompilingException>()
                .setName("ASSIGNED VALUE")
                .setAcceptor(expressionAcceptor)
                .build();

        var semicolon = new State.Builder<VariableHolder, CompilingException>()
                .setName("VARIABLE SEPARATOR")
                .setAcceptor(StateAcceptor.acceptChar(';'))
                .setFinal()
                .build();

        var matrix = TransitionMatrix.chainedTransitions(variableName, assignOperator, expression,
                                                         semicolon);

        return new VariableDeclarationMachine(matrix);
    }
}
