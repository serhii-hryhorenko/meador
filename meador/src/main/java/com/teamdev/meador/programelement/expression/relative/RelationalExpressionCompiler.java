package com.teamdev.meador.programelement.expression.relative;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompiler;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.expression.fsmimpl.relative.RelationalExpressionMachine;
import com.teamdev.meador.programelement.expression.fsmimpl.relative.RelationalExpressionOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.evaluation.operandtype.Value;

import java.util.Optional;

/**
 * {@link ProgramElementCompiler} implementation for compiling Meador relational expressions.
 */
public class RelationalExpressionCompiler implements ProgramElementCompiler {

    private final ProgramElementCompilerFactory compilerFactory;

    public RelationalExpressionCompiler(ProgramElementCompilerFactory factory) {
        this.compilerFactory = Preconditions.checkNotNull(factory);
    }

    @Override
    public Optional<Command> compile(InputSequenceReader reader) throws SyntaxException {
        var relationalExpressionFSM = RelationalExpressionMachine.create(compilerFactory);

        var context = new RelationalExpressionOutputChain();

        if (relationalExpressionFSM.accept(reader, context)) {
            return Optional.of(runtimeEnvironment -> {
                runtimeEnvironment.stack()
                        .create();

                context.left()
                       .execute(runtimeEnvironment);

                Value leftValue = runtimeEnvironment.stack()
                                                    .pop()
                                                    .popResult();

                runtimeEnvironment.stack()
                        .create();

                context.right()
                       .execute(runtimeEnvironment);

                Value rightValue = runtimeEnvironment.stack()
                                                     .pop()
                                                     .popResult();

                runtimeEnvironment.stack()
                        .create();
                runtimeEnvironment.stack()
                                  .peek()
                                  .pushOperand(leftValue);
                runtimeEnvironment.stack()
                                  .peek()
                                  .pushOperand(rightValue);
                runtimeEnvironment.stack()
                                  .peek()
                                  .pushOperator(context.operator());

                Value result = runtimeEnvironment.stack()
                                                 .pop()
                                                 .popResult();
                runtimeEnvironment.stack()
                                  .peek()
                                  .pushOperand(result);
            });
        }

        return Optional.empty();
    }
}
