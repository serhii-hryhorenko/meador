package com.teamdev.meador.fsmimpl.memory_call;

import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompilingException;

/**
 * {@link FiniteStateMachine} implementation for recognizing calls for values stored in memory in Meador programs.
 */
public class MemoryValueFSM extends FiniteStateMachine<MemoryValueContext, CompilingException> {

    public static MemoryValueFSM create() {
        var variableName = new State.Builder<MemoryValueContext, CompilingException>()
                .setName("VARIABLE NAME")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var optionalName = TextIdentifierFSM.execute(inputSequence,
                            new ExceptionThrower<>(CompilingException::new));
                    optionalName.ifPresent(outputSequence::setVariableName);
                    return optionalName.isPresent();
                })
                .setFinite(true)
                .build();

        var dot = new State.Builder<MemoryValueContext, CompilingException>()
                .setName("FIELD REFERENCE OPERATOR")
                .setAcceptor(StateAcceptor.acceptChar('.'))
                .build();

        var fieldName = new State.Builder<MemoryValueContext, CompilingException>()
                .setName("FIELD NAME")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var optionalName = TextIdentifierFSM.execute(inputSequence,
                            new ExceptionThrower<>(CompilingException::new));
                    optionalName.ifPresent(outputSequence::setFieldName);
                    return optionalName.isPresent();
                })
                .setFinite(true)
                .build();

        var matrix = TransitionMatrix.chainedTransitions(variableName, dot, fieldName);

        return new MemoryValueFSM(matrix, new ExceptionThrower<>(CompilingException::new));
    }

    private MemoryValueFSM(TransitionMatrix<MemoryValueContext, CompilingException> transitionMatrix,
                           ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
