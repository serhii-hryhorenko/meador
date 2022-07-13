package com.teamdev.meador.compiler.statement.procedure;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.InputSequence;
import com.teamdev.machine.function.FunctionFSM;
import com.teamdev.meador.ValidatedProcedureFactoryImpl;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.compiler.statement.function.CompileFunctionContext;
import com.teamdev.runtime.Command;

import java.util.Optional;
import java.util.stream.Collectors;

import static com.teamdev.meador.compiler.StatementType.EXPRESSION;

/**
 * {@link StatementCompiler} implementation for compiling procedure statements.
 * Uses {@link FunctionFSM} for filling {@link CompileFunctionContext} output chain.
 */
public class ProcedureCompiler implements StatementCompiler {

    private final StatementCompilerFactory compilerFactory;

    private final ValidatedProcedureFactory factory = new ValidatedProcedureFactoryImpl();

    public ProcedureCompiler(StatementCompilerFactory factory) {
        this.compilerFactory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequence input) throws CompilingException {

        var functionFSM = FunctionFSM.<CompileFunctionContext, CompilingException>create(
                (inputSequence, outputSequence) -> {

                    var optionalCommand = compilerFactory.create(EXPRESSION)
                            .compile(inputSequence);

                    optionalCommand.ifPresent(outputSequence::addCommand);

                    return optionalCommand.isPresent();
                },

                new ExceptionThrower<>(CompilingException::new)
        );

        var context = new CompileFunctionContext();

        if (functionFSM.accept(input, context) &&
                factory.hasProcedure(context.functionName())) {

            var procedure = factory.create(context.functionName());

            if (context.commands()
                    .size() >= procedure.minArguments()
                    && context.commands()
                    .size() <= procedure.maxArguments()) {

                return Optional.of(runtimeEnvironment -> {
                    var values = context.commands()
                            .stream()
                            .map(value -> {
                                runtimeEnvironment.stack()
                                        .create();

                                value.execute(runtimeEnvironment);

                                return runtimeEnvironment.stack()
                                        .pop()
                                        .popResult();
                            })
                            .collect(Collectors.toUnmodifiableList());

                    procedure.accept(values, runtimeEnvironment);
                });
            }

            return Optional.empty();
        }

        return Optional.empty();
    }
}
