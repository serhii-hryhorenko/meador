package com.teamdev.meador.programelement.expression.string;

import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompiler;
import com.teamdev.meador.programelement.expression.fsmimpl.string.StringLiteralMachine;
import com.teamdev.meador.programelement.expression.fsmimpl.string.StringLiteralOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.evaluation.operandtype.StringValue;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for recognizing string literals in Meador programs.
 */
public class StringLiteralCompiler implements ProgramElementCompiler {

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws SyntaxException {
        var outputChain = new StringLiteralOutputChain();

        if (StringLiteralMachine.create()
                .accept(reader, outputChain)) {
            return Optional.of(runtimeEnvironment ->
                                       runtimeEnvironment.stack()
                                                         .peek()
                                                         .pushOperand(new StringValue(
                                                                 outputChain.string())));
        }

        return Optional.empty();
    }
}
