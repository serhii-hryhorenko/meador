package com.teamdev.meador.fsmimpl.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.compiler.StatementType;

public class FieldAssignmentFSM extends FiniteStateMachine<MemoryValueContext, CompilingException> {

    public static FieldAssignmentFSM create(StatementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var structureName = new State.Builder<MemoryValueContext, CompilingException>()
                .setName("STRUCTURE NAME")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var optionalName = TextIdentifierFSM.execute(inputSequence,
                            new ExceptionThrower<>(CompilingException::new));
                    optionalName.ifPresent(outputSequence::setVariableName);
                    return optionalName.isPresent();
                })
                .setTemporary(true)
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
                .build();

        var assignOperator = new State.Builder<MemoryValueContext, CompilingException>()
                .setName("ASSIGN OPERATOR")
                .setAcceptor(StateAcceptor.acceptChar('='))
                .build();

        var value = new State.Builder<MemoryValueContext, CompilingException>()
                .setName("NEW FIELD VALUE")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var optionalCommand = factory.create(StatementType.EXPRESSION).compile(inputSequence);
                    optionalCommand.ifPresent(outputSequence::setFieldValue);
                    return optionalCommand.isPresent();
                })
                .build();

        var semicolon = new State.Builder<MemoryValueContext, CompilingException>()
                .setName("FIELD ASSIGNMENT END")
                .setAcceptor(StateAcceptor.acceptChar(';'))
                .setFinite(true)
                .build();

        var matrix =
                TransitionMatrix.chainedTransitions(structureName, dot, fieldName, assignOperator, value, semicolon);

        return new FieldAssignmentFSM(matrix, new ExceptionThrower<>(CompilingException::new));
    }

    private FieldAssignmentFSM(TransitionMatrix<MemoryValueContext, CompilingException> transitionMatrix,
                               ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
