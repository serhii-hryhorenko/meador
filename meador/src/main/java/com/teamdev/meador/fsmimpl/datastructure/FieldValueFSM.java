package com.teamdev.meador.fsmimpl.datastructure;

import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompilingException;

public class FieldValueFSM extends FiniteStateMachine<FieldAssignmentContext, CompilingException> {

    public static FieldValueFSM create() {
        var structureName = new State.Builder<FieldAssignmentContext, CompilingException>()
                .setName("STRUCTURE NAME")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var optionalName = TextIdentifierFSM.execute(inputSequence,
                            new ExceptionThrower<>(CompilingException::new));
                    optionalName.ifPresent(outputSequence::setStructureName);
                    return optionalName.isPresent();
                })
                .setTemporary(true)
                .build();

        var dot = new State.Builder<FieldAssignmentContext, CompilingException>()
                .setName("FIELD REFERENCE OPERATOR")
                .setAcceptor(StateAcceptor.acceptChar('.'))
                .build();

        var fieldName = new State.Builder<FieldAssignmentContext, CompilingException>()
                .setName("STRUCTURE NAME")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var optionalName = TextIdentifierFSM.execute(inputSequence,
                            new ExceptionThrower<>(CompilingException::new));
                    optionalName.ifPresent(outputSequence::setFieldName);
                    return optionalName.isPresent();
                })
                .setFinite(true)
                .build();

        var matrix = TransitionMatrix.chainedTransitions(structureName, dot, fieldName);

        return new FieldValueFSM(matrix, new ExceptionThrower<>(CompilingException::new));
    }

    private FieldValueFSM(TransitionMatrix<FieldAssignmentContext, CompilingException> transitionMatrix,
                          ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
