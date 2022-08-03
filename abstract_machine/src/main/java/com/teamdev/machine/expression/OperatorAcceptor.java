package com.teamdev.machine.expression;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.runtime.value.operator.AbstractOperatorFactory;

import java.util.function.BiConsumer;


/**
 * {@link StateAcceptor} implementation for recognizing both binary and unary operators in expressions.
 */
public class OperatorAcceptor<T, O, E extends Exception> implements StateAcceptor<O, E> {

    private final AbstractOperatorFactory<T> factory;
    private final BiConsumer<O, T> resultConsumer;

    public OperatorAcceptor(AbstractOperatorFactory<T> factory,
                            BiConsumer<O, T> resultConsumer) {

        this.factory = Preconditions.checkNotNull(factory);
        this.resultConsumer = Preconditions.checkNotNull(resultConsumer);
    }

    @Override
    public boolean accept(InputSequenceReader reader, O outputChain) {
        var operator = new StringBuilder();

        while (reader.canRead()) {
            if (factory.operators().noneMatch(op -> op.startsWith(operator.toString() + reader.read()))) {
                break;
            }

            operator.append(reader.read());
            reader.next();
        }

        if (factory.hasOperator(operator.toString())) {
            resultConsumer.accept(outputChain, factory.create(operator.toString()));
            return true;
        }

        return false;
    }
}
