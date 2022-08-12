package com.teamdev.meador.programelement.util;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElement;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.runtime.Command;

import java.util.function.BiConsumer;

/**
 * {@link FiniteStateMachine} implementation for parsing value expression in parentheses.
 *
 * @param <O> output chain
 */
public class BracketedValueMachine<O> extends FiniteStateMachine<O, SyntaxException> {

    private BracketedValueMachine(TransitionMatrix<O, SyntaxException> transitionMatrix,
                                  ExceptionThrower<SyntaxException> thrower) {
        super(transitionMatrix, thrower);
    }

    public static <O> BracketedValueMachine<O> create(ProgramElementCompilerFactory factory,
                                                      ProgramElement type,
                                                      BiConsumer<O, Command> resultConsumer) {
        Preconditions.checkNotNull(factory, type);

        var openBracket = new State.Builder<O, SyntaxException>()
                .setName("OPEN BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('('))
                .build();

        var expressionToMatch = new State.Builder<O, SyntaxException>()
                .setName("EXPRESSION TO MATCH")
                .setAcceptor(new CompileStatementAcceptor<>(factory, type,
                        Preconditions.checkNotNull(
                                resultConsumer)))
                .build();

        var closeBracket = new State.Builder<O, SyntaxException>()
                .setName("CLOSE BRACKET")
                .setAcceptor(StateAcceptor.acceptChar(')'))
                .setFinal()
                .build();

        var matrix = TransitionMatrix.chainedTransitions(openBracket, expressionToMatch,
                closeBracket);

        return new BracketedValueMachine<>(matrix,
                new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize bracketed " + type + " element.")));
    }
}
