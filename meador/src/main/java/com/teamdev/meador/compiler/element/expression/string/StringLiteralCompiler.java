package com.teamdev.meador.compiler.element.expression.string;

import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.fsmimpl.expression.string.StringLiteralMachine;
import com.teamdev.meador.fsmimpl.expression.string.StringLiteralOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.type.string.StringValue;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for recognizing string literals in Meador programs.
 */
public class StringLiteralCompiler implements ProgramElementCompiler {

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        var outputChain = new StringLiteralOutputChain();

        if (StringLiteralMachine.create().accept(reader, outputChain)) {
            return Optional.of(runtimeEnvironment ->
                    runtimeEnvironment.stack().peek().pushOperand(new StringValue(outputChain.string())));
        }

        return Optional.empty();
    }
}
