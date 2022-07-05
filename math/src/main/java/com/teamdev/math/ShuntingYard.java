package com.teamdev.math;

import com.google.common.base.Preconditions;
import com.teamdev.math.bioperator.AbstractBinaryOperator;
import com.teamdev.math.bioperator.DoubleValueBinaryOperator;
import com.teamdev.math.type.Value;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Double stack data structure for realization of reverse polish notation and evaluating
 * expressions.
 * {@linktourl https://en.wikipedia.org/wiki/Reverse_Polish_notation}
 */

public class ShuntingYard {

    private final Deque<Value> operandStack = new ArrayDeque<>();
    private final Deque<AbstractBinaryOperator> operatorStack = new ArrayDeque<>();

    public void pushOperand(Value operand) {
        operandStack.push(operand);
    }

    public void pushOperator(DoubleValueBinaryOperator operator) {

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
}
