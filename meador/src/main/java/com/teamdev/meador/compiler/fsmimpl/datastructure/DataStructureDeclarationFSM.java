package com.teamdev.meador.compiler.fsmimpl.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;

public class DataStructureDeclarationFSM extends FiniteStateMachine<DataStructureTemplate, CompilingException> {

    public static DataStructureDeclarationFSM create(StatementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var initial = State.<DataStructureTemplate, CompilingException>initialState();

        var structureName = new State.Builder<DataStructureTemplate,
                CompilingException>()
                .setName("DATA STRUCTURE DECLARATION NAME")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var optionalString = TextIdentifierFSM.execute(inputSequence,
                            new ExceptionThrower<>(CompilingException::new));
                    optionalString.ifPresent(outputSequence::setName);
                    return optionalString.isPresent();
                })
                .build();

        var openCurlyBracket = new State.Builder<DataStructureTemplate,
                CompilingException>()
                .setName("DATA STRUCTURE DECLARATION OPEN BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('{'))
                .build();


        var fieldName = new State.Builder<DataStructureTemplate, CompilingException>()
                .setName("DATA STRUCTURE FIELD")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var optionalString = TextIdentifierFSM.execute(inputSequence,
                            new ExceptionThrower<>(CompilingException::new));
                    optionalString.ifPresent(outputSequence::addField);
                    return optionalString.isPresent();
                })
                .build();

        var comma = new State.Builder<DataStructureTemplate, CompilingException>()
                .setName("FIELD SEPARATOR")
                .setAcceptor(StateAcceptor.acceptChar(','))
                .build();

        var closeCurlyBracket = new State.Builder<DataStructureTemplate,
                CompilingException>()
                .setName("DATA STRUCTURE DECLARATION CLOSE BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('}'))
                .setFinite(true)
                .build();

        var matrix = new TransitionMatrixBuilder<DataStructureTemplate,
                CompilingException>()
                .withStartState(initial)
                .allowTransition(initial, structureName)
                .allowTransition(structureName, openCurlyBracket)
                .allowTransition(openCurlyBracket, fieldName)
                .allowTransition(fieldName, closeCurlyBracket, comma)
                .allowTransition(comma, fieldName)
                .build();

        return new DataStructureDeclarationFSM(matrix, new ExceptionThrower<>(CompilingException::new));
    }

    private DataStructureDeclarationFSM(TransitionMatrix<DataStructureTemplate, CompilingException> transitionMatrix,
                                        ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
