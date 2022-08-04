package com.teamdev.meador.fsmimpl.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;

import static com.teamdev.meador.compiler.ProgramElement.EXPRESSION;

/**
 * {@link FiniteStateMachine} implementation for recognizing reassigning data structure fields.
 */
public class FieldAssignmentMachine extends FiniteStateMachine<FieldAssignmentOutputChain, CompilingException> {

    public static FieldAssignmentMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var structureField = new State.Builder<FieldAssignmentOutputChain, CompilingException>()
                .setName("ASSIGNED FIELD")
                .setAcceptor((reader, outputSequence) -> {
                    var field = new FieldReferenceOutputChain();

                    if (DataStructureFieldReferenceMachine.create().accept(reader, field)) {
                        outputSequence.setField(field);
                        return true;
                    }

                    return false;
                })
                .build();

        var assignOperator = new State.Builder<FieldAssignmentOutputChain, CompilingException>()
                .setName("ASSIGN OPERATOR")
                .setAcceptor(StateAcceptor.acceptChar('='))
                .build();

        var value = new State.Builder<FieldAssignmentOutputChain, CompilingException>()
                .setName("NEW FIELD VALUE")
                .setAcceptor(new CompileStatementAcceptor<>(factory, EXPRESSION, FieldAssignmentOutputChain::setCommand))
                .build();

        var semicolon = new State.Builder<FieldAssignmentOutputChain, CompilingException>()
                .setName("FIELD ASSIGNMENT END")
                .setAcceptor(StateAcceptor.acceptChar(';'))
                .setFinal()
                .build();

        var matrix =
                TransitionMatrix.chainedTransitions(structureField, assignOperator, value, semicolon);

        return new FieldAssignmentMachine(matrix, new ExceptionThrower<>(CompilingException::new));
    }

    private FieldAssignmentMachine(TransitionMatrix<FieldAssignmentOutputChain, CompilingException> transitionMatrix,
                                   ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
