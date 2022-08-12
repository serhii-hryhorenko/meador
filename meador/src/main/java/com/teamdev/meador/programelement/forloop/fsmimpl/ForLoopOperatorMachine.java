package com.teamdev.meador.programelement.forloop.fsmimpl;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.util.CodeBlockMachine;
import com.teamdev.meador.programelement.util.CompileStatementAcceptor;

import static com.teamdev.meador.programelement.ProgramElement.BOOLEAN_EXPRESSION;
import static com.teamdev.meador.programelement.ProgramElement.VARIABLE_ASSIGNMENT;

/**
 * {@link FiniteStateMachine} implementation for recognizing the for loop operator in Meador
 * programs.
 * <br>
 * Grammar reference:
 * <pre>
 * {@code
 * for (i = 0; i < 5; i = i + 1;) {
 *      print(i);
 * }
 * }
 * </pre>
 */
public class ForLoopOperatorMachine extends FiniteStateMachine<ForLoopOperatorOutputChain, SyntaxException> {

    private static final String FOR = "for";

    public static ForLoopOperatorMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var exceptionThrower = new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize a for loop operator."));

        var forKeyword = new State.Builder<ForLoopOperatorOutputChain, SyntaxException>()
                .setName("FOR KEYWORD")
                .setAcceptor(
                        (reader, outputSequence) -> TextIdentifierMachine.acceptKeyword(reader, FOR,
                                                                                        exceptionThrower))
                .setTemporary()
                .build();

        var openBracket = new State.Builder<ForLoopOperatorOutputChain, SyntaxException>()
                .setName("OPEN FOR BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('('))
                .build();

        var initVariable = new State.Builder<ForLoopOperatorOutputChain, SyntaxException>()
                .setName("INITIALIZE VARIABLE")
                .setAcceptor(new CompileStatementAcceptor<>(factory,
                                                            VARIABLE_ASSIGNMENT,
                                                            ForLoopOperatorOutputChain::setVariableDeclaration))
                .build();

        var repeatCondition = new State.Builder<ForLoopOperatorOutputChain, SyntaxException>()
                .setName("REPEAT CONDITION")
                .setAcceptor(new CompileStatementAcceptor<>(factory,
                                                            BOOLEAN_EXPRESSION,
                                                            ForLoopOperatorOutputChain::setRepeatCondition))
                .build();

        var repeatSemicolon = new State.Builder<ForLoopOperatorOutputChain, SyntaxException>()
                .setName("SEMICOLON")
                .setAcceptor(StateAcceptor.acceptChar(';'))
                .build();

        var updateVariableStatement = new State.Builder<ForLoopOperatorOutputChain, SyntaxException>()
                .setName("UPDATE VARIABLE EXPRESSION")
                .setAcceptor(new CompileStatementAcceptor<>(factory,
                                                            VARIABLE_ASSIGNMENT,
                                                            ForLoopOperatorOutputChain::setUpdateVariableStatement))
                .build();

        var closeBracket = new State.Builder<ForLoopOperatorOutputChain, SyntaxException>()
                .setName("CLOSE FOR BRACKET")
                .setAcceptor(StateAcceptor.acceptChar(')'))
                .build();

        var loopBody = new State.Builder<ForLoopOperatorOutputChain, SyntaxException>()
                .setName("FOR LOOP BODY")
                .setAcceptor(
                        CodeBlockMachine.create(factory, ForLoopOperatorOutputChain::setLoopBody))
                .setFinal()
                .build();

        var matrix = TransitionMatrix.chainedTransitions(
                forKeyword, openBracket, initVariable, repeatCondition, repeatSemicolon,
                updateVariableStatement, closeBracket, loopBody
        );

        return new ForLoopOperatorMachine(matrix, exceptionThrower);
    }

    private ForLoopOperatorMachine(
            TransitionMatrix<ForLoopOperatorOutputChain, SyntaxException> transitionMatrix,
            ExceptionThrower<SyntaxException> thrower) {
        super(transitionMatrix, thrower);
    }
}
