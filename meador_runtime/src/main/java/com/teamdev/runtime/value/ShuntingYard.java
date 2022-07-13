package com.teamdev.runtime.value;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.bioperator.AbstractBinaryOperator;
import com.teamdev.runtime.value.type.Value;

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
        operandStack.push(operand);
    }

    public void pushOperator(AbstractBinaryOperator operator) {

        while (!operatorStack.isEmpty() && operatorStack.peek()
                .compareTo(operator) >= 0) {
            applyOperand();
        }

        operatorStack.push(operator);
    }

    public Value popResult() {
        applyOperand();
        Preconditions.checkState(operandStack.size() == 1,
                "Stack contains more than 1 operand at the end of calculation.");

        return operandStack.pop();
    }

    private void applyOperand() {
        while (!operatorStack.isEmpty()) {

            var rightOperand = operandStack.pop();
            var leftOperand = operandStack.pop();

            var operator = operatorStack.pop();
            var result = operator.apply(leftOperand, rightOperand);

            operandStack.push(result);
        }
    }

    @Override
    public String toString() {
        return "ShuntingYard{" +
                "operandStack=" + operandStack +
                ", operatorStack=" + operatorStack +
                '}';
    }
}
