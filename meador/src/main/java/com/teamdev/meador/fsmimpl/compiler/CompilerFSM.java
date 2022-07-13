package com.teamdev.meador.fsmimpl.compiler;

import com.teamdev.fsm.*;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.compiler.StatementType;
import com.teamdev.runtime.Command;

import java.util.List;

/**
 * Provides {@link com.teamdev.meador.Meador} general language elements recognition.
 */
public class CompilerFSM extends FiniteStateMachine<List<Command>, CompilingException> {

    private CompilerFSM(TransitionMatrix<List<Command>, CompilingException> transitionMatrix) {
        super(transitionMatrix, new ExceptionThrower<>(CompilingException::new));
    }

    public static CompilerFSM create(StatementCompilerFactory factory) {

        var initial = State.<List<Command>, CompilingException>initialState();

        var begin = new State.Builder<List<Command>, CompilingException>()
                .setName("BEGIN COMPILATION")
                .setAcceptor((inputSequence, outputSequence) -> true)
                .build();

        var procedure = new State.Builder<List<Command>,
                CompilingException>()
                .setName("PROCEDURE")
                .setAcceptor(new CompileStatementAcceptor<List<Command>>(factory,
                        StatementType.PROCEDURE,
                        List::add)
                        .and(StateAcceptor.acceptChar(';'))
                )
                .setFinite(true)
                .setTemporary(true)
                .build();

        var field = new State.Builder<List<Command>, CompilingException>()
                .setName("FIELD ASSIGNMENT")
                .setAcceptor(new CompileStatementAcceptor<>(factory,
                        StatementType.FIELD_ASSIGNMENT,
                        List::add))
                .setFinite(true)
                .setTemporary(true)
                .build();

        var variable = new State.Builder<List<Command>, CompilingException>()
                .setName("VARIABLE")
                .setAcceptor(new CompileStatementAcceptor<>(factory,
                        StatementType.VARIABLE_DECLARATION,
                        List::add))
                .setFinite(true)
                .setTemporary(true)
                .build();

        var dataStructure = new State.Builder<List<Command>,
                CompilingException>()
                .setName("DATA STRUCTURE DECLARATION")
                .setAcceptor(new CompileStatementAcceptor<>(factory,
                        StatementType.DATA_STRUCTURE_DECLARATION,
                        List::add))
                .setFinite(true)
                .setTemporary(true)
                .build();

        var switchOperator = new State.Builder<List<Command>, CompilingException>()
                .setName("SWITCH")
                .setAcceptor(new CompileStatementAcceptor<>(factory, StatementType.SWITCH, List::add))
                .setFinite(true)
                .setTemporary(true)
                .build();

        var matrix = new TransitionMatrixBuilder<List<Command>, CompilingException>()
                .withStartState(initial)
                .allowTransition(initial, begin)
                .allowTransition(begin, switchOperator, procedure, field, variable, dataStructure)
                .allowTransition(field, switchOperator, procedure, field, variable, dataStructure)
                .allowTransition(variable, switchOperator, procedure, field, variable, dataStructure)
                .allowTransition(procedure, switchOperator, procedure, field, variable, dataStructure)
                .allowTransition(switchOperator, switchOperator, procedure, field, variable, dataStructure)
                .allowTransition(dataStructure, switchOperator, procedure, field, variable, dataStructure)
                .build();

        return new CompilerFSM(matrix);
    }
}
