package com.teamdev.meador.fsmimpl.expression.string;

import com.teamdev.fsm.*;
import com.teamdev.meador.compiler.CompilingException;

/**
 * {@link FiniteStateMachine} implementation for parsing Meador string literals.
 */
public class StringLiteralMachine extends FiniteStateMachine<StringLiteralOutputChain, CompilingException> {

    public static StringLiteralMachine create() {
        var exceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var start = new State.Builder<StringLiteralOutputChain, CompilingException>()
                .setName("STRING START")
                .setAcceptor(StateAcceptor.acceptChar('`'))
                .build();

        var literal = new State.Builder<StringLiteralOutputChain, CompilingException>()
                .setName("STRING LITERAL")
                .setAcceptor((reader, outputSequence) -> {
                    var optionalLiteral = StringLiteralValueParser.parse(reader);

                    optionalLiteral.ifPresent(outputSequence::setStringValue);

                    return optionalLiteral.isPresent();
                })
                .build();

        var end = new State.Builder<StringLiteralOutputChain, CompilingException>()
                .setName("STRING START")
                .setAcceptor(StateAcceptor.acceptChar('`'))
                .setFinal()
                .build();

        return new StringLiteralMachine(TransitionMatrix.chainedTransitions(start, literal, end), exceptionThrower);
    }

    private StringLiteralMachine(TransitionMatrix<StringLiteralOutputChain, CompilingException> transitionMatrix,
                                 ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower, false);
    }
}
