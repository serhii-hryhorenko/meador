package com.teamdev.meador.compiler.statement.expression.bool;

import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompiler;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.type.BooleanValue;

import java.util.Optional;

public class BooleanLiteralCompiler implements StatementCompiler {

    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Override
    public Optional<Command> compile(InputSequenceReader inputSequence) throws CompilingException {
        var optionalLiteral = TextIdentifierFSM.execute(inputSequence,
                new ExceptionThrower<>(CompilingException::new));

        if (optionalLiteral.isPresent()) {
            String literal = optionalLiteral.get();

            return switch (literal) {
                case TRUE -> Optional.of(environment -> environment.stack().peek().pushOperand(new BooleanValue(true)));

                case FALSE -> Optional.of(environment -> environment.stack().peek().pushOperand(new BooleanValue(false)));

                default -> Optional.empty();
            };
        }

        return Optional.empty();
    }
}
