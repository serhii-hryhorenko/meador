package com.teamdev.runtime;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.evaluation.TypeMismatchException;
import com.teamdev.runtime.evaluation.operandtype.Value;
import com.teamdev.runtime.evaluation.operator.AbstractBinaryOperator;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Double stack data structure for realization of
 * <a href="https://en.wikipedia.org/wiki/Reverse_Polish_notation"> reverse polish notation </a>
 * and evaluating expressions.
 */

public class ShuntingYard {

    private final Deque<Value> operandStack = new ArrayDeque<>();
    private final Deque<AbstractBinaryOperator> operatorStack = new ArrayDeque<>();

    public void pushOperand(Value operand) {
        operandStack.push(Preconditions.checkNotNull(operand));
    }

    public void pushOperator(AbstractBinaryOperator operator) throws MeadorRuntimeException {
        Preconditions.checkNotNull(operator);

        while (!operatorStack.isEmpty()
                && operatorStack.peek()
                                .compareTo(operator) >= 0) {
            applyOperand();
        }

        operatorStack.push(operator);
    }

    public Value popResult() throws MeadorRuntimeException {
        applyOperand();

        Preconditions.checkState(operandStack.size() == 1,
                                 "Stack contains more than 1 operand at the end of calculation.");

        return Preconditions.checkNotNull(operandStack.pop());
    }

    public Value popOperand() {
        Preconditions.checkState(!operandStack.isEmpty());
        return operandStack.pop();
    }

    public Value peekOperand() {
        Preconditions.checkState(!operandStack.isEmpty());
        return operandStack.peek();
    }

    private void applyOperand() throws MeadorRuntimeException {
        while (!operatorStack.isEmpty()) {

            var rightOperand = operandStack.pop();
            var leftOperand = operandStack.pop();

            var operator = operatorStack.pop();

            try {
                var result = operator.apply(leftOperand, rightOperand);

                operandStack.push(result);
            } catch (TypeMismatchException e) {
                throw new MeadorRuntimeException(e.getMessage());
            }

        }
    }
}
