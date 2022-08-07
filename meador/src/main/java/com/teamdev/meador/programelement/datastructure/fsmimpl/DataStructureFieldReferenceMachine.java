package com.teamdev.meador.programelement.datastructure.fsmimpl;

import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.programelement.CompilingException;

/**
 * {@link FiniteStateMachine} implementation for parsing data structure field referencing.
 */
public class DataStructureFieldReferenceMachine extends FiniteStateMachine<FieldReferenceOutputChain, CompilingException> {

    private DataStructureFieldReferenceMachine(
            TransitionMatrix<FieldReferenceOutputChain, CompilingException> transitionMatrix,
            ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower, false);
    }

    public static DataStructureFieldReferenceMachine create() {
        var exceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var variableName = new State.Builder<FieldReferenceOutputChain, CompilingException>()
                .setName("DATA STRUCTURE VARIABLE NAME")
                .setAcceptor(
                        (reader, outputSequence) -> TextIdentifierMachine.acceptIdentifier(reader,
                                                                                           outputSequence,
                                                                                           FieldReferenceOutputChain::setVariableName,
                                                                                           exceptionThrower))
                .setTemporary()
                .build();

        var dot = new State.Builder<FieldReferenceOutputChain, CompilingException>()
                .setName("DOT")
                .setAcceptor(StateAcceptor.acceptChar('.'))
                .build();

        var fieldName = new State.Builder<FieldReferenceOutputChain, CompilingException>()
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
