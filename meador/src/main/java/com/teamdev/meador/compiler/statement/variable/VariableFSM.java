package com.teamdev.meador.compiler.statement.variable;

import com.google.common.base.Preconditions;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.meador.compiler.CompilingException;

public class VariableFSM extends FiniteStateMachine<VariableHolder, CompilingException> {

    private VariableFSM(TransitionMatrix<VariableHolder, CompilingException> transitionMatrix) {
        super(transitionMatrix, new ExceptionThrower<>(CompilingException::new));
    }

    public static VariableFSM create(StateAcceptor<VariableHolder, CompilingException> expressionAcceptor) {

        Preconditions.checkNotNull(expressionAcceptor);

        var variableName = new State.Builder<VariableHolder, CompilingException>()
                .setName("VARIABLE NAME")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var stringBuilder = new StringBuilder(16);

                    if (TextIdentifierFSM.create(new ExceptionThrower<>(CompilingException::new))
                                         .accept(inputSequence, stringBuilder)) {
                        outputSequence.setName(stringBuilder.toString());
                        return true;
                    }

                    return false;
                })
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
                .setFinite(true)
                .build();

        var matrix =
                TransitionMatrix.chainedTransitions(variableName, assignOperator, expression,
                                                    semicolon);

        return new VariableFSM(matrix);
    }
}
