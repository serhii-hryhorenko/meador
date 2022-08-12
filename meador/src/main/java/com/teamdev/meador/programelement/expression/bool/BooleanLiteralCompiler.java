package com.teamdev.meador.programelement.expression.bool;

import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompiler;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.evaluation.operandtype.BooleanValue;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for recognizing boolean literal values.
 */
public class BooleanLiteralCompiler implements ProgramElementCompiler {

    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws SyntaxException {
        var optionalLiteral = TextIdentifierMachine.parseIdentifier(reader,
                                                                    new ExceptionThrower<>(
                                                                            () -> new SyntaxException("Failed to recognize a boolean literal.")));

        if (optionalLiteral.isPresent()) {
            String literal = optionalLiteral.get();

            return switch (literal) {
                case TRUE -> Optional.of(environment -> environment.stack()
                                                                   .peek()
                                                                   .pushOperand(
                                                                           new BooleanValue(true)));

                case FALSE -> Optional.of(environment -> environment.stack()
                                                                    .peek()
                                                                    .pushOperand(new BooleanValue(
                                                                            false)));

                default -> Optional.empty();
            };
        }

        return Optional.empty();
    }
}
