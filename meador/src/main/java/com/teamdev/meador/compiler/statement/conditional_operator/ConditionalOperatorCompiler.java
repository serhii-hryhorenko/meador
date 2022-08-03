package com.teamdev.meador.compiler.statement.conditional_operator;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.fsmimpl.conditional_operator.ConditionalOperatorOutputChain;
import com.teamdev.meador.fsmimpl.conditional_operator.ConditionalOperatorMachine;
import com.teamdev.meador.fsmimpl.conditional_operator.IfOperatorOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.type.bool.BooleanValueVisitor;
import com.teamdev.runtime.value.type.Value;

import java.util.Optional;

public class ConditionalOperatorCompiler implements ProgramElementCompiler {

    private final ProgramElementCompilerFactory factory;

    public ConditionalOperatorCompiler(ProgramElementCompilerFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        var context = new ConditionalOperatorOutputChain();

        if (ConditionalOperatorMachine.create(factory).accept(reader, context)) {
            return Optional.of(runtimeEnvironment -> {
                var booleanVisitor = new BooleanValueVisitor();

                context.ifOperators().stream()
                        .filter((Predicate<IfOperatorOutputChain>) input -> {
                            runtimeEnvironment.stack().create();
                            input.condition().execute(runtimeEnvironment);
                            Value conditionValue = runtimeEnvironment.stack().pop().popResult();
                            conditionValue.acceptVisitor(booleanVisitor);
                            return booleanVisitor.value();
                        })
                        .findFirst()
                        .ifPresentOrElse(ifOperatorContext -> ifOperatorContext.statements().execute(runtimeEnvironment),
                                () -> {
                                    if (context.elseInstructionPresent()) {
                                        context.elseStatements().execute(runtimeEnvironment);
                                    }
                                }
                        );
            });
        }

        return Optional.empty();
    }
}
