package com.teamdev.meador.programelement.datastructure.fsmimpl;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.fsm.TransitionMatrixBuilder;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.programelement.CompilingException;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.runtime.evaluation.operandtype.DataStructureTemplate;

/**
 * {@link FiniteStateMachine} implementation for recognizing data structure declarations.
 */
public class DataStructureDeclarationMachine extends FiniteStateMachine<DataStructureTemplate, CompilingException> {

    private DataStructureDeclarationMachine(
            TransitionMatrix<DataStructureTemplate, CompilingException> transitionMatrix,
            ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }

    public static DataStructureDeclarationMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var exceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var initial = State.<DataStructureTemplate, CompilingException>initialState();

        var structureName = new State.Builder<DataStructureTemplate,
                CompilingException>()
                .setName("DATA STRUCTURE NAME")
                .setAcceptor(
                        (reader, outputSequence) -> TextIdentifierMachine.acceptIdentifier(reader,
                                                                                           outputSequence,
                                                                                           DataStructureTemplate::setName,
                                                                                           exceptionThrower))
                .setTemporary()
                .build();

        var openCurlyBrace = new State.Builder<DataStructureTemplate,
                CompilingException>()
                .setName("OPEN CURLY BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('{'))
                .build();

        var fieldName = new State.Builder<DataStructureTemplate, CompilingException>()
                .setName("DATA STRUCTURE FIELD")
                .setAcceptor(
                        (reader, outputSequence) -> TextIdentifierMachine.acceptIdentifier(reader,
                                                                                           outputSequence,
                                                                                           DataStructureTemplate::addFieldName,
                                                                                           exceptionThrower))
                .build();

        var comma = new State.Builder<DataStructureTemplate, CompilingException>()
                .setName("FIELD SEPARATOR")
                .setAcceptor(StateAcceptor.acceptChar(','))
                .build();

        var closeCurlyBrace = new State.Builder<DataStructureTemplate,
                CompilingException>()
                .setName("CLOSE CURLY BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('}'))
                .build();

        var semicolon = new State.Builder<DataStructureTemplate, CompilingException>()
                .setName("END DECLARATION")
                .setAcceptor(StateAcceptor.acceptChar(';'))
                .setFinal()
                .build();

        var matrix = new TransitionMatrixBuilder<DataStructureTemplate,
                CompilingException>()

                .withStartState(initial)
                .allowTransition(initial, structureName)
                .allowTransition(structureName, openCurlyBrace)
                .allowTransition(openCurlyBrace, fieldName)
                .allowTransition(fieldName, closeCurlyBrace, comma)
                .allowTransition(comma, fieldName)
                .allowTransition(closeCurlyBrace, semicolon)
                .build();

        return new DataStructureDeclarationMachine(matrix, exceptionThrower);
    }
}
