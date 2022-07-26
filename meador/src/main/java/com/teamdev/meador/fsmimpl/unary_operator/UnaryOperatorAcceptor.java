package com.teamdev.meador.fsmimpl.unary_operator;

import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.runtime.value.operator.unaryoperator.AbstractUnaryOperator;
import com.teamdev.runtime.value.operator.unaryoperator.AbstractUnaryOperatorFactory;
import com.teamdev.runtime.value.operator.unaryoperator.Position;

import java.util.function.BiConsumer;

public class UnaryOperatorAcceptor<O, E extends Exception> implements StateAcceptor<O, E> {

    private final AbstractUnaryOperatorFactory factory;
    private final BiConsumer<O, AbstractUnaryOperator> resultConsumer;
    private final Position position;

    public UnaryOperatorAcceptor(AbstractUnaryOperatorFactory factory, BiConsumer<O, AbstractUnaryOperator> resultConsumer, Position position) {
        this.factory = factory;
        this.resultConsumer = resultConsumer;
        this.position = position;
    }

    @Override
    public boolean accept(InputSequenceReader inputSequence, O outputSequence) {
        var operator = new StringBuilder();

        while (inputSequence.canRead()) {
            if (factory.acceptOperatorPrefix(operator.toString() + inputSequence.read())) {
                operator.append(inputSequence.read());
                inputSequence.next();
            } else {
                break;
            }
        }

        if (factory.acceptOperator(operator.toString(), position)) {
            resultConsumer.accept(outputSequence, factory.create(operator.toString()));
            return true;
        }

        return false;
    }
}
