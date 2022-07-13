package com.teamdev.machine.expression;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequence;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.runtime.value.bioperator.AbstractBinaryOperator;
import com.teamdev.runtime.value.bioperator.AbstractBinaryOperatorFactory;

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
    public boolean accept(InputSequence inputSequence, O outputSequence) {

        if (inputSequence.canRead() &&
                factory.hasOperator(inputSequence.read())) {

            var operator = factory.create(inputSequence.read());
            resultConsumer.accept(outputSequence, operator);
            inputSequence.next();
            return true;
        }

        return false;
    }
}
