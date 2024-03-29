package com.teamdev.meador.programelement.procedure;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.machine.function.FunctionHolder;
import com.teamdev.machine.function.FunctionMachine;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompiler;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.util.CompileStatementAcceptor;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.evaluation.operandtype.Value;
import com.teamdev.runtime.function.ValidatedProcedureFactory;
import com.teamdev.runtime.functionfactoryimpl.ValidatedProcedureFactoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.teamdev.meador.programelement.ProgramElement.EXPRESSION;

/**
 * {@link ProgramElementCompiler} implementation for compiling procedure statements.
 * Uses {@link FunctionMachine} for processing input.
 */
public class ProcedureCompiler implements ProgramElementCompiler {

    private final ProgramElementCompilerFactory compilerFactory;

    private final ValidatedProcedureFactory factory = new ValidatedProcedureFactoryImpl();

    public ProcedureCompiler(ProgramElementCompilerFactory factory) {
        this.compilerFactory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws SyntaxException {

        var functionFSM = FunctionMachine.create(
                        new CompileStatementAcceptor<FunctionHolder<Command>>(compilerFactory, EXPRESSION,
                                                                              FunctionHolder::addArgument),
                        new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize a procedure.")))
                .and(StateAcceptor.acceptChar(';'));

        var context = new FunctionHolder<Command>();

        if (functionFSM.accept(reader, context) &&
                factory.hasProcedure(context.functionName())) {

            var procedure = factory.create(context.functionName());

            if (context.arguments()
                       .size() >= procedure.minArguments()
                    && context.arguments()
                              .size() <= procedure.maxArguments()) {

                return Optional.of(runtimeEnvironment -> {
                    List<Value> values = new ArrayList<>();

                    for (var command : context.arguments()) {
                        runtimeEnvironment.stack()
                                .create();
                        command.execute(runtimeEnvironment);
                        values.add(runtimeEnvironment.stack()
                                                     .pop()
                                                     .popResult());
                    }

                    procedure.accept(values, runtimeEnvironment);
                });
            }

            return Optional.empty();
        }

        return Optional.empty();
    }
}
