package com.teamdev.meador.compiler.statement.string_expr;

import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.meador.fsmimpl.string_expression.StringLiteralFSM;
import com.teamdev.meador.fsmimpl.string_expression.StringLiteralOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.type.StringValue;

import java.util.Optional;

public class StringLiteralCompiler implements StatementCompiler {

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws CompilingException {
        var outputChain = new StringLiteralOutputChain();

        if (StringLiteralFSM.create().accept(reader, outputChain)) {
            return Optional.of(runtimeEnvironment ->
                    runtimeEnvironment.stack().peek().pushOperand(new StringValue(outputChain.string())));
        }

        return Optional.empty();
    }
}
