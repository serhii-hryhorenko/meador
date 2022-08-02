package com.teamdev.meador.fsmimpl.for_loop;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.fsmimpl.util.CodeBlockFSM;

import static com.teamdev.meador.compiler.StatementType.BOOLEAN_EXPRESSION;
import static com.teamdev.meador.compiler.StatementType.VARIABLE_DECLARATION;

/**
 * {@link FiniteStateMachine} implementation for recognizing the for loop operator in Meador programs.
 * Grammar reference:
 * {@code
 * for (i = 0; i < 5; i = i + 1;) {
 * print(i);
 * }
 * }
 */
public class ForLoopOperatorFSM extends FiniteStateMachine<ForLoopOperatorOutputChain, CompilingException> {

    private static final String FOR = "for";

    public static ForLoopOperatorFSM create(StatementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var exceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var forKeyword = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("FOR KEYWORD")
                .setAcceptor((reader, outputSequence) -> TextIdentifierFSM.acceptKeyword(reader, FOR, exceptionThrower))
                .setFinal()
                .build();

        var openBracket = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("OPEN FOR BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('('))
                .build();

        var initVariable = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("INITIALIZE VARIABLE")
                .setAcceptor(new CompileStatementAcceptor<>(factory,
                        VARIABLE_DECLARATION,
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
                        VARIABLE_DECLARATION,
                        ForLoopOperatorOutputChain::setUpdateVariableStatement))
                .build();

        var closeBracket = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("CLOSE FOR BRACKET")
                .setAcceptor(StateAcceptor.acceptChar(')'))
                .build();

        var loopBody = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("FOR LOOP BODY")
                .setAcceptor(CodeBlockFSM.create(factory, ForLoopOperatorOutputChain::setLoopBody))
                .setFinal()
                .build();

        var matrix = TransitionMatrix.chainedTransitions(
                forKeyword, openBracket, initVariable, repeatCondition, repeatSemicolon, updateVariableStatement, closeBracket, loopBody
        );

        return new ForLoopOperatorFSM(matrix, exceptionThrower);
    }

    private ForLoopOperatorFSM(TransitionMatrix<ForLoopOperatorOutputChain, CompilingException> transitionMatrix,
                               ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
