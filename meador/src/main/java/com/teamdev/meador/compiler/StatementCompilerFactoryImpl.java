package com.teamdev.meador.compiler;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.InputSequence;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionOneOfMatrixBuilder;
import com.teamdev.machine.brackets.BracketsFSM;
import com.teamdev.machine.expression.ExpressionFSM;
import com.teamdev.machine.number.NumberFSM;
import com.teamdev.machine.operand.OperandFSM;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.machine.util.ValidatedFunctionFactoryImpl;
import com.teamdev.meador.compiler.statement.function.FunctionCompiler;
import com.teamdev.meador.compiler.statement.procedure.ProcedureCompiler;
import com.teamdev.meador.compiler.statement.variable.VariableFSM;
import com.teamdev.meador.compiler.statement.variable.VariableHolder;
import com.teamdev.meador.runtime.Command;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.teamdev.meador.compiler.StatementType.BRACKETS;
import static com.teamdev.meador.compiler.StatementType.EXPRESSION;
import static com.teamdev.meador.compiler.StatementType.FUNCTION;
import static com.teamdev.meador.compiler.StatementType.NUMBER;
import static com.teamdev.meador.compiler.StatementType.OPERAND;
import static com.teamdev.meador.compiler.StatementType.PROCEDURE;
import static com.teamdev.meador.compiler.StatementType.VARIABLE_DECLARATION;
import static com.teamdev.meador.compiler.StatementType.VARIABLE_VALUE;

public class StatementCompilerFactoryImpl implements StatementCompilerFactory {

    private final Map<StatementType, StatementCompiler> compilers = new EnumMap<>(
            StatementType.class);

    StatementCompilerFactoryImpl() {

        compilers.put(NUMBER, inputSequence ->
                NumberFSM.execute(inputSequence, new ExceptionThrower<>(CompilingException::new))
                         .map(value -> runtimeEnvironment ->
                                 runtimeEnvironment.stack()
                                                   .peek()
                                                   .pushOperand(value))

        );

        compilers.put(EXPRESSION, new CommandListMachineCompiler(createExpressionMachine(),
                                                                 (environment, value) ->
                                                                         environment.stack()
                                                                                    .peek()
                                                                                    .pushOperand(
                                                                                            value)));

        compilers.put(BRACKETS,
                      new CommandListMachineCompiler(BracketsFSM.create(createExpressionMachine(),
                                                                        new ExceptionThrower<>(
                                                                                CompilingException::new)),
                                                     (environment, value) ->
                                                             environment.stack()
                                                                        .peek()
                                                                        .pushOperand(value)));

        compilers.put(OPERAND, new CommandListMachineCompiler(createOperandMachine(),
                                                              (environment, value) ->
                                                                      environment.stack()
                                                                                 .peek()
                                                                                 .pushOperand(
                                                                                         value)));

        compilers.put(FUNCTION, new FunctionCompiler(this,
                                                     new ValidatedFunctionFactoryImpl()));

        compilers.put(PROCEDURE, new ProcedureCompiler(this));

        compilers.put(VARIABLE_DECLARATION, new VariableDeclarationCompiler(this));

        compilers.put(VARIABLE_VALUE, inputSequence -> {

            var variableName = new StringBuilder(16);
            if (TextIdentifierFSM.create(new ExceptionThrower<>(CompilingException::new))
                                 .accept(inputSequence, variableName)) {

                return Optional.of(runtimeEnvironment -> {
                    var value = runtimeEnvironment.memory()
                                                  .getVariable(
                                                          variableName.toString());
                    runtimeEnvironment.stack()
                                      .peek()
                                      .pushOperand(value);
                });

            }

            return Optional.empty();
        });
    }

    private StateAcceptor<List<Command>, CompilingException> createExpressionMachine() {
        return ExpressionFSM.create(
                new CompileStatementAcceptor<>(this, OPERAND, List::add),
                (commands, operator) -> commands.add(
                        runtimeEnvironment -> runtimeEnvironment.stack()
                                                                .peek()
                                                                .pushOperator(operator)),
                new ExceptionThrower<>(CompilingException::new)
        );
    }

    private StateAcceptor<List<Command>, CompilingException> createOperandMachine() {
        return OperandFSM.create(
                new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()

                        .allowTransition(new CompileStatementAcceptor<>(this, NUMBER, List::add),
                                         "MEADOR NUMBER")

                        .allowTransition(new CompileStatementAcceptor<>(this, BRACKETS, List::add),
                                         "MEADOR BRACKETS")

                        .allowTransition(new CompileStatementAcceptor<>(this, FUNCTION, List::add),
                                         "MEADOR FUNCTION")

                        .allowTransition(
                                new CompileStatementAcceptor<>(this, VARIABLE_VALUE, List::add),
                                "MEADOR VARIABLE")

                        .build(),
                new ExceptionThrower<>(CompilingException::new)
        );
    }

    @Override
    public StatementCompiler create(StatementType type) {
        Preconditions.checkArgument(compilers.containsKey(type));

        return compilers.get(type);
    }

    private static class VariableDeclarationCompiler implements StatementCompiler {

        private final StatementCompilerFactory factory;

        private VariableDeclarationCompiler(StatementCompilerFactory factory) {
            this.factory = factory;
        }

        @Override
        public Optional<Command> compile(InputSequence input) throws CompilingException {
            var variable = VariableFSM.create((inputSequence, outputSequence) -> {

                var optionalCommand = factory.create(EXPRESSION)
                                             .compile(inputSequence);

                optionalCommand.ifPresent(outputSequence::setCommand);

                return optionalCommand.isPresent();
            });

            var builder = new VariableHolder();

            if (variable.accept(input, builder)) {
                return Optional.of(runtimeEnvironment -> {

                    runtimeEnvironment.stack()
                                      .create();

                    builder.command()
                           .execute(runtimeEnvironment);

                    runtimeEnvironment.memory()
                                      .putVariable(builder.name(),
                                                   runtimeEnvironment.stack()
                                                                     .pop()
                                                                     .popResult());
                });
            }

            return Optional.empty();
        }
    }

}