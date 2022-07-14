package com.teamdev.meador.fsmimpl.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.runtime.datastructure.DataStructureTemplate;

/**
 * {@link FiniteStateMachine} implementation for recognizing data structure declarations.
 */
public class DataStructureTemplateFSM extends FiniteStateMachine<DataStructureTemplate, CompilingException> {

    public static DataStructureTemplateFSM create(StatementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var initial = State.<DataStructureTemplate, CompilingException>initialState();

        var structureName = new State.Builder<DataStructureTemplate,
                CompilingException>()
                .setName("DATA STRUCTURE NAME")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var optionalString = TextIdentifierFSM.execute(inputSequence,
                            new ExceptionThrower<>(CompilingException::new));
                    optionalString.ifPresent(outputSequence::setName);
                    return optionalString.isPresent();
                })
                .build();

        var openCurlyBrace = new State.Builder<DataStructureTemplate,
                CompilingException>()
                .setName("OPEN CURLY BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('{'))
                .build();

        var fieldName = new State.Builder<DataStructureTemplate, CompilingException>()
                .setName("DATA STRUCTURE FIELD")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var optionalString = TextIdentifierFSM.execute(inputSequence,
                            new ExceptionThrower<>(CompilingException::new));
                    optionalString.ifPresent(outputSequence::addFieldName);
                    return optionalString.isPresent();
                })
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
                .setFinite(true)
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

        return new DataStructureTemplateFSM(matrix, new ExceptionThrower<>(CompilingException::new));
    }

    private DataStructureTemplateFSM(TransitionMatrix<DataStructureTemplate, CompilingException> transitionMatrix,
                                     ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
