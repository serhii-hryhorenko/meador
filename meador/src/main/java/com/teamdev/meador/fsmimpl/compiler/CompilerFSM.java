package com.teamdev.meador.fsmimpl.compiler;

import com.teamdev.fsm.*;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.compiler.StatementType;
import com.teamdev.runtime.Command;

import java.util.List;

import static com.teamdev.meador.compiler.StatementType.FOR;

/**
 * Provides {@link com.teamdev.meador.Meador} general language elements recognition.
 */
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
                .setFinal()
                .setTemporary()
                .build();

        var procedure = new State.Builder<List<Command>,
                CompilingException>()
                .setName("PROCEDURE")
                .setAcceptor(new CompileStatementAcceptor<List<Command>>(factory,
                        StatementType.PROCEDURE,
                        List::add)
                        .and(StateAcceptor.acceptChar(';'))
                )
                .setFinal()
                .setTemporary()
                .build();

        var switchOperator = new State.Builder<List<Command>, CompilingException>()
                .setName("SWITCH")
                .setAcceptor(new CompileStatementAcceptor<>(factory, StatementType.SWITCH, List::add))
                .setFinal()
                .setTemporary()
                .build();

        var forLoopOperator = new State.Builder<List<Command>, CompilingException>()
                .setName("FOR LOOP")
                .setAcceptor(new CompileStatementAcceptor<>(factory, FOR, List::add))
                .setFinal()
                .setTemporary()
                .build();

        var matrix = new TransitionMatrixBuilder<List<Command>, CompilingException>()
                .withStartState(initial)
                .allowTransition(initial, statement)
                .allowTransition(statement, forLoopOperator, switchOperator, procedure, variable)
                .allowTransition(variable, forLoopOperator, switchOperator, procedure, variable)
                .allowTransition(procedure, forLoopOperator, switchOperator, procedure, variable)
                .allowTransition(switchOperator, forLoopOperator, switchOperator, procedure, variable)
                .allowTransition(forLoopOperator, forLoopOperator, switchOperator, procedure, variable)
                .build();

        return new CompilerFSM(matrix);
    }
}
