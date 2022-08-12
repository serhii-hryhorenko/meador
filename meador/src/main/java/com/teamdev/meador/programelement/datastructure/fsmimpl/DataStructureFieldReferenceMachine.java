package com.teamdev.meador.programelement.datastructure.fsmimpl;

import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.programelement.SyntaxException;

/**
 * {@link FiniteStateMachine} implementation for parsing data structure field referencing.
 */
public class DataStructureFieldReferenceMachine extends FiniteStateMachine<FieldReferenceOutputChain, SyntaxException> {

    private DataStructureFieldReferenceMachine(
            TransitionMatrix<FieldReferenceOutputChain, SyntaxException> transitionMatrix,
            ExceptionThrower<SyntaxException> thrower) {
        super(transitionMatrix, thrower, false);
    }

    public static DataStructureFieldReferenceMachine create() {
        var exceptionThrower = new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize reference to a data structure field."));

        var variableName = new State.Builder<FieldReferenceOutputChain, SyntaxException>()
                .setName("DATA STRUCTURE VARIABLE NAME")
                .setAcceptor(
                        (reader, outputSequence) -> TextIdentifierMachine.acceptIdentifier(reader,
                                                                                           outputSequence,
                                                                                           FieldReferenceOutputChain::setVariableName,
                                                                                           exceptionThrower))
                .setTemporary()
                .build();

        var dot = new State.Builder<FieldReferenceOutputChain, SyntaxException>()
                .setName("DOT")
                .setAcceptor(StateAcceptor.acceptChar('.'))
                .build();

        var fieldName = new State.Builder<FieldReferenceOutputChain, SyntaxException>()
                .setName("FIELD NAME")
                .setAcceptor(
                        (reader, outputSequence) -> TextIdentifierMachine.acceptIdentifier(reader,
                                                                                           outputSequence,
                                                                                           FieldReferenceOutputChain::setFieldName,
                                                                                           exceptionThrower))
                .setFinal()
                .build();

        return new DataStructureFieldReferenceMachine(
                TransitionMatrix.chainedTransitions(variableName, dot, fieldName),
                exceptionThrower);
    }
}
