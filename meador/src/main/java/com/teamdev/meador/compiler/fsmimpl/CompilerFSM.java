package com.teamdev.meador.compiler.fsmimpl;

import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.fsm.TransitionMatrixBuilder;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.compiler.StatementType;
import com.teamdev.meador.runtime.Command;

import java.util.List;

public class CompilerFSM extends FiniteStateMachine<List<Command>, CompilingException> {

    private CompilerFSM(TransitionMatrix<List<Command>, CompilingException> transitionMatrix) {
        super(transitionMatrix, new ExceptionThrower<>(CompilingException::new));
    }

    public static CompilerFSM create(StatementCompilerFactory factory) {

        var initial = State.<List<Command>, CompilingException>initialState();

        var statement = new State.Builder<List<Command>, CompilingException>()
                .setName("STATEMENT RECOGNITION")
                .setAcceptor((inputSequence, outputSequence) -> true)
                .build();

        var variable = new State.Builder<List<Command>, CompilingException>()
                .setName("VARIABLE")
                .setAcceptor(new CompileStatementAcceptor<>(factory,
                                                            StatementType.VARIABLE_DECLARATION,
                                                            List::add))
                .setFinite(true)
                .setTemporary(true)
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

        var end = new State.Builder<List<Command>, CompilingException>()
                .setName("COMPILATION END")
                .setAcceptor((inputSequence, outputSequence) -> !inputSequence.canRead())
                .setFinite(true)
                .build();

        var matrix = new TransitionMatrixBuilder<List<Command>,
                                                                                            CompilingException>()
                .withStartState(initial)
                .allowTransition(initial, statement)
                .allowTransition(statement, procedure, variable)
                .allowTransition(variable, end, procedure, variable)
                .allowTransition(procedure, end, procedure, variable)
                .build();

        return new CompilerFSM(matrix);
    }
}
