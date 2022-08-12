package com.teamdev.meador.programelement.expression.fsmimpl.string;

import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.meador.programelement.SyntaxException;

/**
 * {@link FiniteStateMachine} implementation for parsing Meador string literals.
 */
public class StringLiteralMachine extends FiniteStateMachine<StringLiteralOutputChain, SyntaxException> {

    private StringLiteralMachine(
            TransitionMatrix<StringLiteralOutputChain, SyntaxException> transitionMatrix,
            ExceptionThrower<SyntaxException> thrower) {
        super(transitionMatrix, thrower, false);
    }

    public static StringLiteralMachine create() {
        var exceptionThrower = new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize a string literal."));

        var start = new State.Builder<StringLiteralOutputChain, SyntaxException>()
                .setName("STRING START")
                .setAcceptor(StateAcceptor.acceptChar('`'))
                .build();

        var literal = new State.Builder<StringLiteralOutputChain, SyntaxException>()
                .setName("STRING LITERAL")
                .setAcceptor((reader, outputSequence) -> {
                    var optionalLiteral = StringLiteralValueParser.parse(reader);

                    optionalLiteral.ifPresent(outputSequence::setStringValue);

                    return optionalLiteral.isPresent();
                })
                .build();

        var end = new State.Builder<StringLiteralOutputChain, SyntaxException>()
                .setName("STRING START")
                .setAcceptor(StateAcceptor.acceptChar('`'))
                .setFinal()
                .build();

        return new StringLiteralMachine(TransitionMatrix.chainedTransitions(start, literal, end),
                                        exceptionThrower);
    }
}
