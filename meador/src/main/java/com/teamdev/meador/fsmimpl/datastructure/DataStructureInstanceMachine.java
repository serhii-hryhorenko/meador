package com.teamdev.meador.fsmimpl.datastructure;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;

import static com.teamdev.meador.compiler.ProgramElement.EXPRESSION;

/**
 * {@link FiniteStateMachine} implementation for recognizing data structures implementations in Meador programs.
 */
public class DataStructureInstanceMachine extends FiniteStateMachine<DataStructureOutputChain, CompilingException> {

    public static DataStructureInstanceMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var exceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var initial = State.<DataStructureOutputChain, CompilingException>initialState();

        var templateName = new State.Builder<DataStructureOutputChain, CompilingException>()
                .setName("TEMPLATE NAME")
                .setAcceptor((reader, outputSequence) -> TextIdentifierMachine.acceptIdentifier(reader, outputSequence,
                        DataStructureOutputChain::setTemplateName,
                        exceptionThrower))
                .setTemporary()
                .build();

        var openCurlyBrace = new State.Builder<DataStructureOutputChain, CompilingException>()
                .setName("OPEN CURLY BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('{'))
                .build();

        var structureField = new State.Builder<DataStructureOutputChain, CompilingException>()
                .setName("STRUCTURE FIELD")
                .setAcceptor(new CompileStatementAcceptor<>(factory, EXPRESSION, DataStructureOutputChain::addValue))
                .build();

        var comma = new State.Builder<DataStructureOutputChain, CompilingException>()
                .setName("FIELD SEPARATOR")
                .setAcceptor(StateAcceptor.acceptChar(','))
                .build();

        var closeCurlyBrace = new State.Builder<DataStructureOutputChain, CompilingException>()
                .setName("CLOSE CURLY BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('}'))
                .setFinal()
                .build();

        var matrix = new TransitionMatrixBuilder<DataStructureOutputChain, CompilingException>()
                .withStartState(initial)
                .allowTransition(initial, templateName)
                .allowTransition(templateName, openCurlyBrace)
                .allowTransition(openCurlyBrace, structureField)
                .allowTransition(structureField, closeCurlyBrace, comma)
                .allowTransition(comma, structureField)
                .build();

        return new DataStructureInstanceMachine(matrix, exceptionThrower);
    }

    private DataStructureInstanceMachine(TransitionMatrix<DataStructureOutputChain, CompilingException> transitionMatrix,
                                         ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
