package com.teamdev.meador.compiler.statement.function;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.InputSequence;
import com.teamdev.machine.function.FunctionFSM;
import com.teamdev.machine.function.ValidatedFunctionFactory;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.runtime.Command;

import java.util.Optional;
import java.util.stream.Collectors;

import static com.teamdev.meador.compiler.StatementType.NUMERIC_EXPRESSION;

public class FunctionCompiler implements StatementCompiler {

    private final StatementCompilerFactory compilerFactory;
    private final ValidatedFunctionFactory functionFactory;

    public FunctionCompiler(StatementCompilerFactory compilerFactory,
                            ValidatedFunctionFactory functionFactory) {

        this.compilerFactory = Preconditions.checkNotNull(compilerFactory);
        this.functionFactory = Preconditions.checkNotNull(functionFactory);
    }

    @Override
    public Optional<Command> compile(InputSequence input) throws CompilingException {

        var functionFSM = FunctionFSM.<CompileFunctionContext, CompilingException>create(
                (inputSequence, outputSequence) -> {

                    var optionalCommand = compilerFactory.create(NUMERIC_EXPRESSION)
                            .compile(inputSequence);

                    optionalCommand.ifPresent(outputSequence::addCommand);

                    return optionalCommand.isPresent();
                },

                new ExceptionThrower<>(CompilingException::new)
        );

        var context = new CompileFunctionContext();

        if (functionFSM.accept(input, context) &&
                functionFactory.hasFunction(context.functionName())) {

            var function = functionFactory.create(context.functionName());

            if (context.arguments()
                    .size() >= function.getMinArguments()
                    && context.arguments()
                    .size() <= function.getMaxArguments()) {

                return Optional.of(runtimeEnvironment -> {

                    var doubles = context.commands()
                            .stream()
                            .map(value -> {
                                runtimeEnvironment.stack().create();

                                value.execute(runtimeEnvironment);

                                return runtimeEnvironment.stack().pop().popResult();
                            })
                            .collect(Collectors.toUnmodifiableList());

                    runtimeEnvironment.stack().peek().pushOperand(function.apply(doubles));
                });
            }

            return Optional.empty();
        }

        return Optional.empty();
    }
}
