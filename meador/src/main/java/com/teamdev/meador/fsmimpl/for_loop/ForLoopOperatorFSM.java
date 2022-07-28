package com.teamdev.meador.fsmimpl.for_loop;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.compiler.StatementType;
import com.teamdev.meador.fsmimpl.util.CodeBlockFSM;

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
                .setTemporary(true)
                .build();

        var openBracket = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("OPEN FOR BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('('))
                .build();

        var initVariable = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("INITIALIZE VARIABLE")
                .setAcceptor(new CompileStatementAcceptor<>(factory,
                        StatementType.VARIABLE_DECLARATION,
                        ForLoopOperatorOutputChain::setVariableDeclaration))
                .build();

        var repeatCondition = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("REPEAT CONDITION")
                .setAcceptor(new CompileStatementAcceptor<>(factory,
                        StatementType.RELATIONAL_EXPRESSION,
                        ForLoopOperatorOutputChain::setRepeatCondition))
                .build();

        var repeatSemicolon = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("SEMICOLON")
                .setAcceptor(StateAcceptor.acceptChar(';'))
                .build();

        var updateVariableStatement = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("UPDATE VARIABLE STATEMENT")
                .setAcceptor(new CompileStatementAcceptor<>(factory,
                        StatementType.VARIABLE_DECLARATION,
                        ForLoopOperatorOutputChain::setUpdateVariableStatement))
                .build();

        var closeBracket = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("CLOSE FOR BRACKET")
                .setAcceptor(StateAcceptor.acceptChar(')'))
                .build();

        var loopBody = new State.Builder<ForLoopOperatorOutputChain, CompilingException>()
                .setName("LOOP BODY")
                .setAcceptor(CodeBlockFSM.create(factory, ForLoopOperatorOutputChain::setLoopBody))
                .setFinite(true)
                .build();

        var matrix = TransitionMatrix.chainedTransitions(forKeyword, openBracket, initVariable, repeatCondition, repeatSemicolon, updateVariableStatement, closeBracket, loopBody);

        return new ForLoopOperatorFSM(matrix, exceptionThrower);
    }

    private ForLoopOperatorFSM(TransitionMatrix<ForLoopOperatorOutputChain, CompilingException> transitionMatrix,
                               ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
