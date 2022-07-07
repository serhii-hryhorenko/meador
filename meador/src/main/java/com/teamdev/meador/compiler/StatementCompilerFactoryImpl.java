package com.teamdev.meador.compiler;

import com.google.common.base.Function;
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
import com.teamdev.math.MathBinaryOperatorFactoryImpl;
import com.teamdev.math.type.Value;
import com.teamdev.meador.compiler.fsmimpl.CompilerFSM;
import com.teamdev.meador.compiler.fsmimpl.RelationalExpressionFSM;
import com.teamdev.meador.compiler.fsmimpl.switch_operator.SwitchFSM;
import com.teamdev.meador.compiler.statement.relative_expr.RelationalExpressionContext;
import com.teamdev.meador.compiler.statement.function.FunctionCompiler;
import com.teamdev.meador.compiler.statement.procedure.ProcedureCompiler;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchContext;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchOptionContext;
import com.teamdev.meador.compiler.statement.variable.VariableFSM;
import com.teamdev.meador.compiler.statement.variable.VariableHolder;
import com.teamdev.meador.runtime.Command;
import com.teamdev.meador.runtime.RuntimeEnvironment;
import org.checkerframework.checker.units.qual.C;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.teamdev.meador.compiler.StatementType.*;

public class StatementCompilerFactoryImpl implements StatementCompilerFactory {

    private final Map<StatementType, StatementCompiler> compilers = new EnumMap<>(
            StatementType.class);

    public StatementCompilerFactoryImpl() {

        compilers.put(PROGRAM, compilerInput -> {
            var commands = new ArrayList<Command>();

            if (CompilerFSM.create(this)
                    .accept(compilerInput, commands)) {

                return Optional.of(
                        runtimeEnvironment -> commands.forEach(
                                command -> command.execute(runtimeEnvironment)));
            }

            return Optional.empty();
        });

        compilers.put(SWITCH, inputSequence -> {
            var context = new SwitchContext();

            if (SwitchFSM.create(StatementCompilerFactoryImpl.this).accept(inputSequence, context)) {
                return Optional.of(runtimeEnvironment -> {
                    runtimeEnvironment.stack().create();
                    context.value().execute(runtimeEnvironment);

                    Value matchedValue = runtimeEnvironment.stack().pop().popResult();

                    context.options().stream()
                            .filter(switchOptionContext -> {
                                Command condition = switchOptionContext.condition();
                                runtimeEnvironment.stack().create();
                                condition.execute(runtimeEnvironment);
                                Value conditionValue = runtimeEnvironment.stack().pop().popResult();
                                return matchedValue.equals(conditionValue);
                            })
                            .findFirst()
                            .ifPresentOrElse(switchOptionContext -> switchOptionContext.statement().execute(runtimeEnvironment),
                                    () -> context.defaultCommand().execute(runtimeEnvironment));
                });
            }
            return Optional.empty();
        });

        compilers.put(NUMBER, inputSequence ->
                NumberFSM.execute(inputSequence, new ExceptionThrower<>(CompilingException::new))
                         .map(value -> runtimeEnvironment ->
                                 runtimeEnvironment.stack()
                                                   .peek()
                                                   .pushOperand(value))

        );

        compilers.put(EXPRESSION, new CommandListMachineCompiler(
                OperandFSM.create(new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()
                                .allowTransition(new CompileStatementAcceptor<>(this, RELATIONAL_EXPRESSION, List::add),
                                        "RELATIONAL EXPRESSION", true)
                                .allowTransition(new CompileStatementAcceptor<>(this, NUMERIC_EXPRESSION, List::add),
                                "NUMERIC EXPRESSION")
                                .build(),
                        new ExceptionThrower<>(CompilingException::new)
                ),
                (environment, value) -> environment.stack().peek().pushOperand(value)
        ));

        compilers.put(NUMERIC_EXPRESSION, new CommandListMachineCompiler(createNumericExpressionMachine(),
                                                                 (environment, value) ->
                                                                         environment.stack()
                                                                                    .peek()
                                                                                    .pushOperand(value)));

        compilers.put(RELATIONAL_EXPRESSION, inputSequence -> {
            var relationalExpressionFSM = RelationalExpressionFSM.create(this);

            var context = new RelationalExpressionContext();

            if (relationalExpressionFSM.accept(inputSequence, context)) {
                return Optional.of(runtimeEnvironment -> {
                    runtimeEnvironment.stack().create();

                    context.left().execute(runtimeEnvironment);

                    Value leftValue = runtimeEnvironment.stack().pop().popResult();

                    runtimeEnvironment.stack().create();

                    context.right().execute(runtimeEnvironment);

                    Value rightValue = runtimeEnvironment.stack().pop().popResult();

                    runtimeEnvironment.stack().create();
                    runtimeEnvironment.stack().peek().pushOperand(leftValue);
                    runtimeEnvironment.stack().peek().pushOperand(rightValue);
                    runtimeEnvironment.stack().peek().pushOperator(context.operator());

                    Value result = runtimeEnvironment.stack().pop().popResult();
                    runtimeEnvironment.stack().peek().pushOperand(result);
                });
            }

            return Optional.empty();
        });

        compilers.put(BRACKETS,
                      new CommandListMachineCompiler(BracketsFSM.create(createNumericExpressionMachine(),
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
                                                                                 .pushOperand(value)));

        compilers.put(FUNCTION, new FunctionCompiler(this,
                                                     new ValidatedFunctionFactoryImpl()));

        compilers.put(PROCEDURE, new ProcedureCompiler(this));

        compilers.put(VARIABLE_DECLARATION, new VariableDeclarationCompiler(this));

        compilers.put(VARIABLE_VALUE, inputSequence -> {

            var variableName = new StringBuilder(16);
            if (TextIdentifierFSM.create(new ExceptionThrower<>(CompilingException::new))
                                 .accept(inputSequence, variableName)) {

                return Optional.of(runtimeEnvironment -> {
                    var value = runtimeEnvironment.memory().getVariable(variableName.toString());
                    runtimeEnvironment.stack()
                                      .peek()
                                      .pushOperand(value);
                });

            }

            return Optional.empty();
        });
    }

    private StateAcceptor<List<Command>, CompilingException> createNumericExpressionMachine() {
        return ExpressionFSM.create(
                new CompileStatementAcceptor<>(this, OPERAND, List::add),
                new MathBinaryOperatorFactoryImpl(),
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
                                         "MEADOR FUNCTION", true)

                        .allowTransition(new CompileStatementAcceptor<>(this, VARIABLE_VALUE, List::add),
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