package com.teamdev.meador.fsmimpl.for_loop;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.fsmimpl.util.CodeBlockMachine;

import static com.teamdev.meador.compiler.ProgramElement.BOOLEAN_EXPRESSION;
import static com.teamdev.meador.compiler.ProgramElement.VARIABLE_ASSIGNMENT;

/**
 * {@link FiniteStateMachine} implementation for recognizing the for loop operator in Meador programs.
 * <br>
 * Grammar reference:
 * <pre>
 * for (i = 0; i < 5; i = i + 1;) {
 *      print(i);
 * }
 * </pre>
 */
public class ForLoopOperatorMachine extends FiniteStateMachine<ForLoopOperatorOutputChain, CompilingException> {

    private static final String FOR = "for";

    public static ForLoopOperatorMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var exceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var forKeyword = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("FOR KEYWORD")
                .setAcceptor((reader, outputSequence) -> TextIdentifierMachine.acceptKeyword(reader, FOR, exceptionThrower))
                .setTemporary()
                .build();

        var openBracket = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("OPEN FOR BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('('))
                .build();

        var initVariable = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("INITIALIZE VARIABLE")
                .setAcceptor(new CompileStatementAcceptor<>(factory,
                        VARIABLE_ASSIGNMENT,
                        ForLoopOperatorOutputChain::setVariableDeclaration))
                .build();

        var repeatCondition = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("REPEAT CONDITION")
                .setAcceptor(new CompileStatementAcceptor<>(factory,
                        BOOLEAN_EXPRESSION,
                        ForLoopOperatorOutputChain::setRepeatCondition))
                .build();

        var repeatSemicolon = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("SEMICOLON")
                .setAcceptor(StateAcceptor.acceptChar(';'))
                .build();

        var updateVariableStatement = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("UPDATE VARIABLE EXPRESSION")
                .setAcceptor(new CompileStatementAcceptor<>(factory,
                        VARIABLE_ASSIGNMENT,
                        ForLoopOperatorOutputChain::setUpdateVariableStatement))
                .build();

        var closeBracket = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("CLOSE FOR BRACKET")
                .setAcceptor(StateAcceptor.acceptChar(')'))
                .build();

        var loopBody = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("FOR LOOP BODY")
                .setAcceptor(CodeBlockMachine.create(factory, ForLoopOperatorOutputChain::setLoopBody))
                .setFinal()
                .build();

        var matrix = TransitionMatrix.chainedTransitions(
                forKeyword, openBracket, initVariable, repeatCondition, repeatSemicolon, updateVariableStatement, closeBracket, loopBody
        );

        return new ForLoopOperatorMachine(matrix, exceptionThrower);
    }

    private ForLoopOperatorMachine(TransitionMatrix<ForLoopOperatorOutputChain, CompilingException> transitionMatrix,
                                   ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
