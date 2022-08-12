package com.teamdev.meador.programelement.util;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.runtime.Command;

import java.util.function.BiConsumer;

import static com.teamdev.meador.programelement.ProgramElement.LIST_OF_STATEMENTS;

/**
 * {@link FiniteStateMachine} implementation for recognizing Meador Statements inside curly braces.
 *
 * @param <O>
 *         output chain
 */
public class CodeBlockMachine<O> extends FiniteStateMachine<O, SyntaxException> {

    private CodeBlockMachine(TransitionMatrix<O, SyntaxException> transitionMatrix,
                             ExceptionThrower<SyntaxException> thrower) {
        super(transitionMatrix, thrower);
    }

    public static <O> CodeBlockMachine<O> create(ProgramElementCompilerFactory factory,
                                                 BiConsumer<O, Command> blockConsumer) {

        Preconditions.checkNotNull(factory);

        var openCurlyBracket = new State.Builder<O, SyntaxException>()
                .setName("CODE BLOCK START")
                .setAcceptor(StateAcceptor.acceptChar('{'))
                .build();

        var program = new State.Builder<O, SyntaxException>()
                .setName("CODE BLOCK")
                .setAcceptor(
                        new CompileStatementAcceptor<>(factory, LIST_OF_STATEMENTS, blockConsumer))
                .build();

        var closeCurlyBracket = new State.Builder<O, SyntaxException>()
                .setName("CODE BLOCK END")
                .setAcceptor(StateAcceptor.acceptChar('}'))
                .setFinal()
                .build();

        var matrix = TransitionMatrix.chainedTransitions(openCurlyBracket, program,
                                                         closeCurlyBracket);

        return new CodeBlockMachine<>(matrix, new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize a block of code.")));
    }
}
