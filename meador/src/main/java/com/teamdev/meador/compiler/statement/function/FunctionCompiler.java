package com.teamdev.meador.compiler.statement.function;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.machine.function.FunctionHolder;
import com.teamdev.machine.function.FunctionMachine;
import com.teamdev.machine.function.ValidatedFunctionFactory;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.type.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.teamdev.meador.compiler.ProgramElement.NUMERIC_EXPRESSION;

public class FunctionCompiler implements ProgramElementCompiler {

    private final ProgramElementCompilerFactory compilerFactory;
    private final ValidatedFunctionFactory functionFactory;

    public FunctionCompiler(ProgramElementCompilerFactory compilerFactory,
                            ValidatedFunctionFactory functionFactory) {

        this.compilerFactory = Preconditions.checkNotNull(compilerFactory);
        this.functionFactory = Preconditions.checkNotNull(functionFactory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {

        var functionFSM = FunctionMachine.<Command, CompilingException>create(
                (inputSequence, outputSequence) -> {

                    var optionalCommand = compilerFactory.create(NUMERIC_EXPRESSION)
                            .compile(inputSequence);

                    optionalCommand.ifPresent(outputSequence::addArgument);

                    return optionalCommand.isPresent();
                },

                new ExceptionThrower<>(CompilingException::new)
        );

        var context = new FunctionHolder<Command>();

        if (functionFSM.accept(reader, context) &&
                functionFactory.hasFunction(context.functionName())) {

            var function = functionFactory.create(context.functionName());

            if (context.arguments().size() >= function.getMinArguments()
                    && context.arguments().size() <= function.getMaxArguments()) {

                return Optional.of(runtimeEnvironment -> {

                    List<Value> doubles = new ArrayList<>();

                    for (var command : context.arguments()) {
                        runtimeEnvironment.stack().create();
                        command.execute(runtimeEnvironment);
                        doubles.add(runtimeEnvironment.stack().pop().popResult());
                    }

                    runtimeEnvironment.stack().peek().pushOperand(function.apply(doubles));
                });
            }

            return Optional.empty();
        }

        return Optional.empty();
    }
}
