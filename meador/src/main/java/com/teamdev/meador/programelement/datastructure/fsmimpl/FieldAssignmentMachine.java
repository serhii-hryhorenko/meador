package com.teamdev.meador.programelement.datastructure.fsmimpl;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.meador.programelement.CompilingException;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.util.CompileStatementAcceptor;

import static com.teamdev.meador.programelement.ProgramElement.EXPRESSION;

/**
 * {@link FiniteStateMachine} implementation for recognizing reassigning data structure fields.
 */
public class FieldAssignmentMachine extends FiniteStateMachine<FieldAssignmentOutputChain, CompilingException> {

    private FieldAssignmentMachine(
            TransitionMatrix<FieldAssignmentOutputChain, CompilingException> transitionMatrix,
            ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }

    public static FieldAssignmentMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var structureField = new State.Builder<FieldAssignmentOutputChain, CompilingException>()
                .setName("ASSIGNED FIELD")
                .setAcceptor((reader, outputSequence) -> {
                    var field = new FieldReferenceOutputChain();

                    if (DataStructureFieldReferenceMachine.create()
                            .accept(reader, field)) {
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
                .setAcceptor(new CompileStatementAcceptor<>(factory, EXPRESSION,
                                                            FieldAssignmentOutputChain::setCommand))
                .build();

        var semicolon = new State.Builder<FieldAssignmentOutputChain, CompilingException>()
                .setName("FIELD ASSIGNMENT END")
                .setAcceptor(StateAcceptor.acceptChar(';'))
                .setFinal()
                .build();

        var matrix =
                TransitionMatrix.chainedTransitions(structureField, assignOperator, value,
                                                    semicolon);

        return new FieldAssignmentMachine(matrix, new ExceptionThrower<>(CompilingException::new));
    }
}
