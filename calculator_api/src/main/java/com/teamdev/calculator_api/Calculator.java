package com.teamdev.calculator_api;

import com.google.common.base.Preconditions;
import com.teamdev.calculator_api.resolver.MathElementResolverFactory;
import com.teamdev.calculator_api.resolver.ResolvingException;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.ShuntingYard;
import com.teamdev.runtime.evaluation.TypeMismatchException;
import com.teamdev.runtime.evaluation.operandtype.NumericValueVisitor;
import com.teamdev.runtime.evaluation.operandtype.Value;

/**
 * An API for resolving of math expressions. Math expression may contain:
 * - integer and float numbers;
 * - binary operators with priorities, e.g. +, -, *, /, ^(power operator);
 * - brackets;
 * - functions, e.g. sum, average, sin, cos, pi.
 * Raises a specific error if math expression is incorrect.
 * Typical usage scenario:
 * {@code
 * MathExpression expression = new MathExpression("2+2*3");
 * Calculator calculator = new Calculator();
 * Result result = calculator.calculate(expression);
 * }
 * Implementation details: uses a set of finite state machines in order to
 * define grammar of math expression, parse elements of expression and calculate the result.
 */
public class Calculator {

    private final MathElementResolverFactory factory = new MathElementResolverFactoryImpl();

    private static void raiseException(InputSequenceReader inputChain) throws
                                                                       InvalidExpressionException {
        throw new InvalidExpressionException("Wrong mathematical expression",
                                             inputChain.getPosition());
    }

    public Output calculate(MathExpression expression) throws InvalidExpressionException,
                                                              MeadorRuntimeException {
        Preconditions.checkNotNull(expression);
        var calculatorFSM = CalculatorMachine.create(factory);

        var inputChain = new InputSequenceReader(expression.getSource());
        var outputChain = new ShuntingYard();

        try {
            if (!calculatorFSM.accept(inputChain, outputChain)) {

                raiseException(inputChain);
            }
        } catch (ResolvingException e) {
            raiseException(inputChain);
        }

        Value value = outputChain.popResult();
        NumericValueVisitor visitor = new NumericValueVisitor();
        try {
            value.acceptVisitor(visitor);
        } catch (TypeMismatchException e) {
            e.printStackTrace();
        }
        return new Output(visitor.value());
    }
}
