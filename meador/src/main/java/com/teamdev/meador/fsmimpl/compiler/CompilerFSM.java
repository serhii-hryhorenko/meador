package com.teamdev.meador.fsmimpl.compiler;

import com.teamdev.fsm.*;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.runtime.Command;

import java.util.List;

import static com.teamdev.meador.compiler.StatementType.*;

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
                .setAcceptor((inputSequence, outputSequence) -> inputSequence.canRead())
                .build();

        var variable = new State.Builder<List<Command>, CompilingException>()
                .setName("VARIABLE")
                .setAcceptor(new CompileStatementAcceptor<>(factory, VARIABLE_DECLARATION, List::add))
                .setFinal()
                .setTemporary()
                .build();

        var procedure = new State.Builder<List<Command>, CompilingException>()
                .setName("PROCEDURE")
                .setAcceptor(new CompileStatementAcceptor<List<Command>>(factory, PROCEDURE, List::add)
                        .and(StateAcceptor.acceptChar(';')))
                .setFinal()
                .setTemporary()
                .build();

        var switchOperator = new State.Builder<List<Command>, CompilingException>()
                .setName("SWITCH")
                .setAcceptor(new CompileStatementAcceptor<>(factory, SWITCH, List::add))
                .setFinal()
                .setTemporary()
                .build();

        var conditionalOperator = new State.Builder<List<Command>, CompilingException>()
                .setName("CONDITIONAL OPERATOR")
                .setAcceptor(new CompileStatementAcceptor<>(factory, CONDITIONAL_OPERATOR, List::add))
                .setFinal()
                .setTemporary()
                .build();

        var forOperator = new State.Builder<List<Command>, CompilingException>()
                .setName("FOR LOOP OPERATOR")
                .setAcceptor(new CompileStatementAcceptor<>(factory, FOR, List::add))
                .setFinal()
                .setTemporary()
                .build();

        var matrix = new TransitionMatrixBuilder<List<Command>, CompilingException>()
                .withStartState(initial)
                .allowTransition(initial, statement)
                .allowTransition(statement, conditionalOperator, switchOperator, forOperator, procedure, variable)
                .allowTransition(variable, conditionalOperator, switchOperator, forOperator, procedure, variable)
                .allowTransition(procedure, conditionalOperator, switchOperator, forOperator, procedure, variable)
                .allowTransition(switchOperator, conditionalOperator, switchOperator, forOperator, procedure, variable, forOperator)
                .allowTransition(conditionalOperator, conditionalOperator, switchOperator, forOperator, procedure, variable)
                .allowTransition(forOperator, conditionalOperator, switchOperator, forOperator, procedure, variable)
                .build();

        return new CompilerFSM(matrix);
    }
}
