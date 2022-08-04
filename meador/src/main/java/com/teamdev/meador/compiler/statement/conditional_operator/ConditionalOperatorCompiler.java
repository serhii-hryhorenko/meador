package com.teamdev.meador.compiler.statement.conditional_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.fsmimpl.conditional_operator.ConditionalOperatorMachine;
import com.teamdev.meador.fsmimpl.conditional_operator.ConditionalOperatorOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.type.Value;
import com.teamdev.runtime.value.type.bool.BooleanValueVisitor;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for recognizing Meador conditional operators.
 * Implementation provides conditional branching as in Java.
 * Curly braces are always required.
 * Grammar reference:
 * <pre>
 * if (condition) {
 *     list_of_statements
 * } else if (condition2) {
 *     list_of_statements
 * } else {
 *     list_of_statements
 * }
 * </pre>
 */
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

                for (var operator : context.conditionalOperators()) {
                    runtimeEnvironment.stack().create();
                    operator.condition().execute(runtimeEnvironment);
                    Value conditionValue = runtimeEnvironment.stack().pop().popResult();

                    conditionValue.acceptVisitor(booleanVisitor);

                    if (booleanVisitor.value()) {
                        operator.statements().execute(runtimeEnvironment);
                        return;
                    }
                }

                if (context.elseInstructionPresent()) {
                    context.elseStatementList().execute(runtimeEnvironment);
                }
            });
        }

        return Optional.empty();
    }
}
