package com.teamdev.meador.fsmimpl.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.compiler.StatementType;

public class DataStructureFSM extends FiniteStateMachine<DataStructureContext, CompilingException> {

    public static DataStructureFSM create(StatementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var initial = State.<DataStructureContext, CompilingException>initialState();

        var templateName = new State.Builder<DataStructureContext, CompilingException>()
                .setName("TEMPLATE NAME")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var optionalTemplateName = TextIdentifierFSM.execute(inputSequence,
                            new ExceptionThrower<>(ClassCastException::new));

                    optionalTemplateName.ifPresent(outputSequence::setTemplateName);
                    return optionalTemplateName.isPresent();
                })
                .build();

        var openCurlyBrace = new State.Builder<DataStructureContext, CompilingException>()
                .setName("OPEN CURLY BRACE")
                .setAcceptor(StateAcceptor.acceptChar('{'))
                .build();

        var structureField = new State.Builder<DataStructureContext, CompilingException>()
                .setName("IMPLEMENTED STRUCTURE FIELD")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var optionalCommand = factory.create(StatementType.EXPRESSION).compile(inputSequence);
                    optionalCommand.ifPresent(outputSequence::addValue);
                    return optionalCommand.isPresent();
                })
                .build();

        var comma = new State.Builder<DataStructureContext, CompilingException>()
                .setName("FIELD SEPARATOR")
                .setAcceptor(StateAcceptor.acceptChar(','))
                .build();

        var closeCurlyBrace = new State.Builder<DataStructureContext, CompilingException>()
                .setName("CLOSE CURLY BRACE")
                .setAcceptor(StateAcceptor.acceptChar('}'))
                .build();

        var semicolon = new State.Builder<DataStructureContext, CompilingException>()
                .setName("SEMICOLON")
                .setAcceptor(StateAcceptor.acceptChar(';'))
                .setFinite(true)
                .build();

        var matrix = new TransitionMatrixBuilder<DataStructureContext, CompilingException>()
                .withStartState(initial)
                .allowTransition(initial, templateName)
                .allowTransition(templateName, openCurlyBrace)
                .allowTransition(openCurlyBrace, structureField)
                .allowTransition(structureField, closeCurlyBrace, comma)
                .allowTransition(comma, structureField)
                .allowTransition(closeCurlyBrace, semicolon)
                .build();

        return new DataStructureFSM(matrix, new ExceptionThrower<>(CompilingException::new));
    }

    private DataStructureFSM(TransitionMatrix<DataStructureContext, CompilingException> transitionMatrix,
                             ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
