package com.teamdev.meador.programelement.datastructure.fsmimpl;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.fsm.TransitionMatrixBuilder;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.util.CompileStatementAcceptor;

import static com.teamdev.meador.programelement.ProgramElement.EXPRESSION;

/**
 * {@link FiniteStateMachine} implementation for recognizing data structures implementations in
 * Meador programs.
 */
public class DataStructureInstanceMachine extends FiniteStateMachine<DataStructureOutputChain, SyntaxException> {

    private DataStructureInstanceMachine(
            TransitionMatrix<DataStructureOutputChain, SyntaxException> transitionMatrix,
            ExceptionThrower<SyntaxException> thrower) {
        super(transitionMatrix, thrower);
    }

    public static DataStructureInstanceMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var exceptionThrower = new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize creation of data structure instance."));

        var initial = State.<DataStructureOutputChain, SyntaxException>initialState();

        var templateName = new State.Builder<DataStructureOutputChain, SyntaxException>()
                .setName("TEMPLATE NAME")
                .setAcceptor(
                        (reader, outputSequence) -> TextIdentifierMachine.acceptIdentifier(reader,
                                                                                           outputSequence,
                                                                                           DataStructureOutputChain::setTemplateName,
                                                                                           exceptionThrower))
                .setTemporary()
                .build();

        var openCurlyBrace = new State.Builder<DataStructureOutputChain, SyntaxException>()
                .setName("OPEN CURLY BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('{'))
                .build();

        var structureField = new State.Builder<DataStructureOutputChain, SyntaxException>()
                .setName("STRUCTURE FIELD")
                .setAcceptor(new CompileStatementAcceptor<>(factory, EXPRESSION,
                                                            DataStructureOutputChain::addValue))
                .build();

        var comma = new State.Builder<DataStructureOutputChain, SyntaxException>()
                .setName("FIELD SEPARATOR")
                .setAcceptor(StateAcceptor.acceptChar(','))
                .build();

        var closeCurlyBrace = new State.Builder<DataStructureOutputChain, SyntaxException>()
                .setName("CLOSE CURLY BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('}'))
                .setFinal()
                .build();

        var matrix = new TransitionMatrixBuilder<DataStructureOutputChain, SyntaxException>()
                .withStartState(initial)
                .allowTransition(initial, templateName)
                .allowTransition(templateName, openCurlyBrace)
                .allowTransition(openCurlyBrace, structureField)
                .allowTransition(structureField, closeCurlyBrace, comma)
                .allowTransition(comma, structureField)
                .build();

        return new DataStructureInstanceMachine(matrix, exceptionThrower);
    }
}
