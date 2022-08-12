package com.teamdev.meador.programelement.expression.fsmimpl.string;

import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.fsm.State;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.fsm.TransitionMatrixBuilder;
import com.teamdev.machine.util.SymbolAcceptor;
import com.teamdev.meador.programelement.SyntaxException;

import java.util.Optional;

/**
 * {@link FiniteStateMachine} implementation for parsing string literal value.
 */
public class StringLiteralValueParser extends FiniteStateMachine<StringBuilder, SyntaxException> {

    private StringLiteralValueParser(
            TransitionMatrix<StringBuilder, SyntaxException> transitionMatrix,
            ExceptionThrower<SyntaxException> thrower) {
        super(transitionMatrix, thrower, false);
    }

    public static Optional<String> parse(InputSequenceReader reader) throws SyntaxException {
        var literal = new StringBuilder();

        if (create().accept(reader, literal)) {
            return Optional.of(literal.toString());
        }

        return Optional.empty();
    }

    private static StringLiteralValueParser create() {
        var initial = State.<StringBuilder, SyntaxException>initialState();

        var symbol = new State.Builder<StringBuilder, SyntaxException>()
                .setName("SYMBOL")
                .setAcceptor(new SymbolAcceptor<>(character -> character != '`'))
                .setFinal()
                .build();

        var matrix = new TransitionMatrixBuilder<StringBuilder, SyntaxException>()
                .withStartState(initial)
                .allowTransition(initial, symbol)
                .allowTransition(symbol, symbol)
                .build();

        return new StringLiteralValueParser(matrix,
                                            new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize a string literal value.")));
    }
}
