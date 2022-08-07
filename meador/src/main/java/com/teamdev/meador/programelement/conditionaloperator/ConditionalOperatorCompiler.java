package com.teamdev.meador.programelement.conditionaloperator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.programelement.CompilingException;
import com.teamdev.meador.programelement.ProgramElementCompiler;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.conditionaloperator.fsmimpl.ConditionalOperatorMachine;
import com.teamdev.meador.programelement.conditionaloperator.fsmimpl.ConditionalOperatorOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.RuntimeEnvironment;

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

        if (ConditionalOperatorMachine.create(factory)
                .accept(reader, context)) {
            return Optional.of(new Command() {
                @Override
                public void execute(RuntimeEnvironment environment) throws
                                                                           MeadorRuntimeException {
                    for (var operator : context.conditionalOperators()) {
                        if (operator.condition().checkCondition(environment)) {
                            operator.statements()
                                    .execute(environment);
                            return;
                        }
                    }

                    if (context.elseInstructionPresent()) {
                        context.elseStatementList()
                               .execute(environment);
                    }
                }
            });
        }

        return Optional.empty();
    }
}
