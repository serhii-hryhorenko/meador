package com.teamdev.machine.expression;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequence;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.math.PrioritizedBinaryOperatorFactoryImpl;
import com.teamdev.math.bioperator.DoubleValueBinaryOperator;
import com.teamdev.math.bioperator.PrioritizedBinaryOperatorFactory;

import java.util.function.BiConsumer;

/**
 * {@link StateAcceptor} implementation for recognizing binary operators and providing
 * math operations on operands for {@link ExpressionFSM}.
 */
public class BinaryOperatorAcceptor<O, E extends Exception> implements StateAcceptor<O, E> {

    private final PrioritizedBinaryOperatorFactory prioritizedBinaryOperatorFactory
            = new PrioritizedBinaryOperatorFactoryImpl();

    private final BiConsumer<O, DoubleValueBinaryOperator> resultConsumer;

    BinaryOperatorAcceptor(
            BiConsumer<O, DoubleValueBinaryOperator> resultConsumer) {

        this.resultConsumer = Preconditions.checkNotNull(resultConsumer);
    }

    @Override
    public boolean accept(InputSequence inputSequence, O outputSequence) {

        if (inputSequence.canRead() &&
                prioritizedBinaryOperatorFactory.hasOperator(inputSequence.read())) {

            var operator = prioritizedBinaryOperatorFactory.create(inputSequence.read());
            resultConsumer.accept(outputSequence, operator);
            inputSequence.next();
            return true;
        }

        return false;
    }
}
