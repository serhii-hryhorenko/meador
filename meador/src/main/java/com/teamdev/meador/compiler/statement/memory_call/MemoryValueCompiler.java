package com.teamdev.meador.compiler.statement.memory_call;

import com.teamdev.fsm.InputSequence;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.meador.fsmimpl.memory_call.MemoryValueContext;
import com.teamdev.meador.fsmimpl.memory_call.MemoryValueFSM;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.type.DataStructureVisitor;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * {@link StatementCompiler} implementation for compiling memory call statements.
 * Works for both variables and data structure fields.
 * Uses {@link MemoryValueFSM} and {@link MemoryValueContext} for parsing.
 */
public class MemoryValueCompiler implements StatementCompiler {

    @Override
    public Optional<Command> compile(InputSequence inputSequence) throws CompilingException {
        var context = new MemoryValueContext();

        if (MemoryValueFSM.create().accept(inputSequence, context)) {
            if (Objects.isNull(context.fieldName())) {
                return Optional.of(runtimeEnvironment -> {
                    var optionalValue = runtimeEnvironment.memory().
                            getVariable(context.variableName());

                    optionalValue.ifPresentOrElse(
                            value -> runtimeEnvironment.stack()
                                    .peek()
                                    .pushOperand(value),
                            () -> {
                                throw new NoSuchElementException();
                            }
                    );
                });
            }

            return Optional.of(runtimeEnvironment -> {
                var optionalStructure = runtimeEnvironment.memory().getVariable(context.variableName());

                var visitor = new DataStructureVisitor();

                optionalStructure.orElseThrow().acceptVisitor(visitor);

                var fieldValue = visitor.value()
                        .getField(context.fieldName())
                        .orElseThrow()
                        .command();

                fieldValue.execute(runtimeEnvironment);
            });
        }

        return Optional.empty();
    }
}
