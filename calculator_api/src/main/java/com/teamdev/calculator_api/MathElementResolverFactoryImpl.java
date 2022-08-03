package com.teamdev.calculator_api;

import com.teamdev.calculator_api.resolver.*;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.TransitionOneOfMatrixBuilder;
import com.teamdev.machine.brackets.BracketsMachine;
import com.teamdev.machine.expression.ExpressionMachine;
import com.teamdev.machine.function.FunctionMachine;
import com.teamdev.machine.function.FunctionHolder;
import com.teamdev.machine.number.NumberMachine;
import com.teamdev.machine.operand.OperandMachine;
import com.teamdev.machine.util.ValidatedFunctionFactoryImpl;
import com.teamdev.runtime.value.MathBinaryOperatorFactoryImpl;
import com.teamdev.runtime.value.ShuntingYard;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import static com.teamdev.calculator_api.resolver.MathElement.*;

public class MathElementResolverFactoryImpl implements MathElementResolverFactory {

    private final Map<MathElement, MathElementResolver> resolvers = new EnumMap<>(
            MathElement.class);

    public MathElementResolverFactoryImpl() {

        resolvers.put(EXPRESSION, new ShuntingYardResolver(ExpressionMachine.create(
                new ResolveMathElementAcceptor<>(this, OPERAND, ShuntingYard::pushOperand),
                new MathBinaryOperatorFactoryImpl(),
                ShuntingYard::pushOperator,
                new ExceptionThrower<>(ResolvingException::new)))
        );

        resolvers.put(OPERAND, new ShuntingYardResolver(OperandMachine.create(
                        new TransitionOneOfMatrixBuilder<ShuntingYard, ResolvingException>()
                                .allowTransition(new ResolveMathElementAcceptor<>(this, NUMBER,
                                        ShuntingYard::pushOperand))
                                .allowTransition(new ResolveMathElementAcceptor<>(this, BRACKETS,
                                        ShuntingYard::pushOperand))
                                .allowTransition(new ResolveMathElementAcceptor<>(this, FUNCTION,
                                        ShuntingYard::pushOperand))
                                .build(),
                        new ExceptionThrower<>(ResolvingException::new))
                )
        );

        resolvers.put(BRACKETS, new ShuntingYardResolver(
                        BracketsMachine.create(
                                ExpressionMachine.create(
                                        new ResolveMathElementAcceptor<>(this,
                                                EXPRESSION,
                                                ShuntingYard::pushOperand),
                                        new MathBinaryOperatorFactoryImpl(),
                                        ShuntingYard::pushOperator,
                                        new ExceptionThrower<>(ResolvingException::new)),

                                new ExceptionThrower<>(ResolvingException::new))
                )
        );

        resolvers.put(NUMBER, input -> NumberMachine.execute(input, new ExceptionThrower<>(
                ResolvingException::new)));

        resolvers.put(FUNCTION, input -> {

            var argument = new ResolveMathElementAcceptor<>(
                    this,
                    EXPRESSION,
                    FunctionHolder::addArgument);

            var factory = new ValidatedFunctionFactoryImpl();

            var functionFSM = FunctionMachine.create(argument,
                    new ExceptionThrower<>(ResolvingException::new));
            var holder = new FunctionHolder();

            if (functionFSM.accept(input, holder) &&
                    factory.hasFunction(holder.functionName())) {

                var function = factory.create(holder.functionName());

                if (holder.arguments()
                        .size() >= function.getMinArguments()
                        && holder.arguments()
                        .size() <= function.getMaxArguments()) {

                    return Optional.of(function.apply(holder.arguments()));
                }

                return Optional.empty();
            }

            return Optional.empty();
        });
    }

    @Override
    public MathElementResolver create(MathElement machine) {
        return resolvers.get(machine);
    }
}
