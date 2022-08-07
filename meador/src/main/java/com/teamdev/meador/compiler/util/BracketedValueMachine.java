package com.teamdev.meador.compiler.util;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElement;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.runtime.Command;

import java.util.function.BiConsumer;

/**
 * {@link FiniteStateMachine} implementation for parsing value expression in parentheses.
 *
 * @param <O>
 *         output chain
 */
public class BracketedValueMachine<O> extends FiniteStateMachine<O, CompilingException> {

    private BracketedValueMachine(TransitionMatrix<O, CompilingException> transitionMatrix,
                                  ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }

    public static <O> BracketedValueMachine<O> create(ProgramElementCompilerFactory factory,
                                                      ProgramElement type,
                                                      BiConsumer<O, Command> resultConsumer) {
        Preconditions.checkNotNull(factory, type);

        var openBracket = new State.Builder<O, CompilingException>()
                .setName("OPEN BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('('))
                .build();

        var expressionToMatch = new State.Builder<O, CompilingException>()
                .setName("EXPRESSION TO MATCH")
                .setAcceptor(new CompileStatementAcceptor<>(factory, type,
                                                            Preconditions.checkNotNull(
                                                                    resultConsumer)))
                .build();

        var closeBracket = new State.Builder<O, CompilingException>()
                .setName("CLOSE BRACKET")
                .setAcceptor(StateAcceptor.acceptChar(')'))
                .setFinal()
                .build();

        var matrix = TransitionMatrix.chainedTransitions(openBracket, expressionToMatch,
                                                         closeBracket);

        return new BracketedValueMachine<>(matrix, new ExceptionThrower<>(CompilingException::new));
    }
}
