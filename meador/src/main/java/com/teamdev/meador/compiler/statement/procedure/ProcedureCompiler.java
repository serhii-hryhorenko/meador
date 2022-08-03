package com.teamdev.meador.compiler.statement.procedure;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.machine.function.FunctionMachine;
import com.teamdev.meador.ValidatedProcedureFactoryImpl;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.compiler.statement.function.CompileFunctionContext;
import com.teamdev.runtime.Command;

import java.util.Optional;

import static com.teamdev.meador.compiler.ProgramElement.EXPRESSION;

/**
 * {@link ProgramElementCompiler} implementation for compiling procedure statements.
 * Uses {@link FunctionMachine} for filling {@link CompileFunctionContext} output chain.
 */
public class ProcedureCompiler implements ProgramElementCompiler {

    private final ProgramElementCompilerFactory compilerFactory;

    private final ValidatedProcedureFactory factory = new ValidatedProcedureFactoryImpl();

    public ProcedureCompiler(ProgramElementCompilerFactory factory) {
        this.compilerFactory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {

        var functionFSM = FunctionMachine.create(
                new CompileStatementAcceptor<>(compilerFactory, EXPRESSION, CompileFunctionContext::addCommand),
                new ExceptionThrower<>(CompilingException::new))
                .and(StateAcceptor.acceptChar(';'));

        var context = new CompileFunctionContext();

        if (functionFSM.accept(reader, context) &&
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
                            .toList();

                    procedure.accept(values, runtimeEnvironment);
                });
            }

            return Optional.empty();
        }

        return Optional.empty();
    }
}
