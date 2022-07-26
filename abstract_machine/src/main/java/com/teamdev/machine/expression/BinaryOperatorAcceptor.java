package com.teamdev.machine.expression;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.runtime.value.operator.bioperator.AbstractBinaryOperator;
import com.teamdev.runtime.value.operator.bioperator.AbstractBinaryOperatorFactory;

import java.util.function.BiConsumer;


/**
 * {@link StateAcceptor} implementation for recognizing binary operators and providing
 * math operations on operands for {@link ExpressionFSM}.
 */
public class BinaryOperatorAcceptor<O, E extends Exception> implements StateAcceptor<O, E> {

    private final AbstractBinaryOperatorFactory factory;
    private final BiConsumer<O, AbstractBinaryOperator> resultConsumer;

    public BinaryOperatorAcceptor(AbstractBinaryOperatorFactory factory,
                                  BiConsumer<O, AbstractBinaryOperator> resultConsumer) {

        this.factory = Preconditions.checkNotNull(factory);
        this.resultConsumer = Preconditions.checkNotNull(resultConsumer);
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

        if (factory.acceptOperator(operator.toString())) {
            resultConsumer.accept(outputSequence, factory.create(operator.toString()));
            return true;
        }

        return false;
    }
}
