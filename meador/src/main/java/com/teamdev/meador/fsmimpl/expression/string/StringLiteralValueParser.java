package com.teamdev.meador.fsmimpl.expression.string;

import com.teamdev.fsm.*;
import com.teamdev.machine.util.SymbolAcceptor;
import com.teamdev.meador.compiler.CompilingException;

import java.util.Optional;

public class StringLiteralValueParser extends FiniteStateMachine<StringBuilder, CompilingException> {

    public static Optional<String> parse(InputSequenceReader reader) throws CompilingException {
        var literal = new StringBuilder();

        if (create().accept(reader, literal)) {
            return Optional.of(literal.toString());
        }

        return Optional.empty();
    }

    public static StringLiteralValueParser create() {
        var initial = State.<StringBuilder, CompilingException>initialState();

        var symbol = new State.Builder<StringBuilder, CompilingException>()
                .setName("SYMBOL")
                .setAcceptor(new SymbolAcceptor<>(character -> character != '`'))
                .setFinite(true)
                .build();

        var matrix = new TransitionMatrixBuilder<StringBuilder, CompilingException>()
                .withStartState(initial)
                .allowTransition(initial, symbol)
                .allowTransition(symbol, symbol)
                .build();

        return new StringLiteralValueParser(matrix, new ExceptionThrower<>(CompilingException::new));
    }

    private StringLiteralValueParser(TransitionMatrix<StringBuilder, CompilingException> transitionMatrix,
                                     ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower, false);
    }
}
