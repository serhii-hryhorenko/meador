package com.teamdev.meador.compiler.statement.conditional_operator;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.fsmimpl.conditional_operator.ConditionalOperatorContext;
import com.teamdev.meador.fsmimpl.conditional_operator.ConditionalOperatorFSM;
import com.teamdev.meador.fsmimpl.conditional_operator.IfOperatorContext;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.type.BooleanVisitor;
import com.teamdev.runtime.value.type.Value;

import java.util.Optional;

public class ConditionalOperatorCompiler implements StatementCompiler {

    private final StatementCompilerFactory factory;

    public ConditionalOperatorCompiler(StatementCompilerFactory factory) {
        this.factory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader inputSequence) throws CompilingException {
        var context = new ConditionalOperatorContext();

        if (ConditionalOperatorFSM.create(factory).accept(inputSequence, context)) {
            return Optional.of(runtimeEnvironment -> {
                var booleanVisitor = new BooleanVisitor();

                context.ifOperators().stream()
                        .filter((Predicate<IfOperatorContext>) input -> {
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
