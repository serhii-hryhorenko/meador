package com.teamdev.meador.fsmimpl.string_expression;

import com.teamdev.fsm.*;
import com.teamdev.meador.compiler.CompilingException;

public class StringLiteralFSM extends FiniteStateMachine<StringLiteralOutputChain, CompilingException> {

    public static StringLiteralFSM create() {
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
                .setFinite(true)
                .build();

        return new StringLiteralFSM(TransitionMatrix.chainedTransitions(start, literal, end), exceptionThrower);
    }

    private StringLiteralFSM(TransitionMatrix<StringLiteralOutputChain, CompilingException> transitionMatrix,
                             ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower, false);
    }
}
