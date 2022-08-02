package com.teamdev.meador;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionOneOfMatrixBuilder;
import com.teamdev.machine.brackets.BracketsFSM;
import com.teamdev.machine.expression.ExpressionFSM;
import com.teamdev.machine.number.NumberFSM;
import com.teamdev.machine.util.ValidatedFunctionFactoryImpl;
import com.teamdev.meador.compiler.*;
import com.teamdev.meador.compiler.statement.conditional_operator.ConditionalOperatorCompiler;
import com.teamdev.meador.compiler.statement.expression.bool.BooleanLiteralCompiler;
import com.teamdev.meador.compiler.statement.expression.relative.RelationalExpressionCompiler;
import com.teamdev.meador.compiler.statement.expression.string.StringLiteralCompiler;
import com.teamdev.meador.compiler.statement.for_loop.ForLoopOperatorCompiler;
import com.teamdev.meador.compiler.statement.function.FunctionCompiler;
import com.teamdev.meador.compiler.statement.procedure.ProcedureCompiler;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchOperatorCompiler;
import com.teamdev.meador.compiler.statement.unary_operator.UnaryPostfixExpressionCompiler;
import com.teamdev.meador.compiler.statement.unary_operator.UnaryPrefixExpressionCompiler;
import com.teamdev.meador.compiler.statement.variable.VariableDeclarationCompiler;
import com.teamdev.meador.compiler.statement.variable.VariableValueCompiler;
import com.teamdev.meador.fsmimpl.compiler.CompilerFSM;
import com.teamdev.meador.fsmimpl.util.DeepestParsedInputAcceptor;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.BooleanBinaryOperatorFactory;
import com.teamdev.runtime.value.MathBinaryOperatorFactoryImpl;
import com.teamdev.runtime.value.StringBinaryOperatorFactory;
import com.teamdev.runtime.value.UnaryOperatorFactory;
import com.teamdev.runtime.value.operator.bioperator.AbstractBinaryOperator;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static com.teamdev.meador.compiler.StatementType.*;

public class StatementCompilerFactoryImpl implements StatementCompilerFactory {

    private final Map<StatementType, StatementCompiler> compilers = new EnumMap<>(
            StatementType.class);

    public StatementCompilerFactoryImpl() {
        var compilingExceptionThrower = new ExceptionThrower<>(CompilingException::new);

        Supplier<StateAcceptor<List<Command>, CompilingException>> numericOperand = () -> FiniteStateMachine.oneOf(
                "NUMERIC OPERAND",
                new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, NUMERIC_BRACKETS, List::add)
                                .named("MEADOR BRACKETS"))

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, FUNCTION, List::add)
                                .named("MEADOR FUNCTION"))

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, READ_VARIABLE, List::add)
                                .named("MEADOR VARIABLE"))

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, NUMBER, List::add)
                                .named("MEADOR NUMBER")),

                compilingExceptionThrower
        );

        Supplier<StateAcceptor<List<Command>, CompilingException>> booleanOperand = () -> FiniteStateMachine.oneOf(
                "BOOLEAN OPERAND",
                new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()
                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, BOOLEAN_LITERAL, List::add)
                                .named("MEADOR BOOLEAN LITERAL"), true)

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, BOOLEAN_BRACKETS, List::add)
                                .named("MEADOR BOOLEAN BRACKETS"))

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, RELATIONAL_EXPRESSION, List::add)
                                .named("MEADOR RELATIVE EXPRESSION"), true)

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, READ_VARIABLE, List::add)
                                .named("MEADOR VARIABLE")),

                compilingExceptionThrower
        );

        Supplier<StateAcceptor<List<Command>, CompilingException>> stringOperand = () -> FiniteStateMachine.oneOf("STRING OPERAND",
                new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()
                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, STRING_LITERAL, List::add)
                                .named("STRING LITERAL"))

                        .allowTransition(new DeepestParsedInputAcceptor<>(
                                ArrayList::new,
                                booleanOperand.get(),
                                numericOperand.get())),

                compilingExceptionThrower
        );

        BiConsumer<List<Command>, AbstractBinaryOperator> pushBinaryOperator = (commands, operator) ->
                commands.add(environment -> environment.stack()
                        .peek()
                        .pushOperator(operator));

        Supplier<ExpressionFSM<List<Command>, CompilingException>> numericExpression = () -> ExpressionFSM.create(
                new CompileStatementAcceptor<>(this, NUMERIC_OPERAND, List::add),
                new MathBinaryOperatorFactoryImpl(),
                pushBinaryOperator,
                compilingExceptionThrower
        );

        Supplier<ExpressionFSM<List<Command>, CompilingException>> booleanExpression = () -> ExpressionFSM.create(
                new CompileStatementAcceptor<>(this, BOOLEAN_OPERAND, List::add),
                new BooleanBinaryOperatorFactory(),
                pushBinaryOperator,
                compilingExceptionThrower
        );

        Supplier<ExpressionFSM<List<Command>, CompilingException>> stringExpression = () -> ExpressionFSM.create(
                stringOperand.get(),
                new StringBinaryOperatorFactory(),
                pushBinaryOperator,
                compilingExceptionThrower
        );

        Supplier<DetachedStackStatementCompiler> expressionAcceptor = () -> new DetachedStackStatementCompiler(
                new DeepestParsedInputAcceptor<>(
                        ArrayList::new,

                        new CompileStatementAcceptor<List<Command>>(this, BOOLEAN_EXPRESSION, List::add)
                                .named("BOOLEAN EXPRESSION"),

                        new CompileStatementAcceptor<List<Command>>(this, NUMERIC_EXPRESSION, List::add)
                                .named("NUMERIC EXPRESSION"),

                        new CompileStatementAcceptor<List<Command>>(this, STRING_EXPRESSION, List::add)
                                .named("STRING EXPRESSION")
                )
        );

        Supplier<StatementCompiler> programAcceptor = () -> reader -> {
            var commands = new ArrayList<Command>();

            if (CompilerFSM.create(StatementCompilerFactoryImpl.this).accept(reader, commands)) {
                return Optional.of(environment -> commands.forEach(command -> command.execute(environment)));
            }

            return Optional.empty();
        };

        compilers.put(PROGRAM, programAcceptor.get());

        Supplier<StatementCompiler> numberAcceptor = () -> inputSequence ->
                NumberFSM.execute(inputSequence, compilingExceptionThrower)
                        .map(value -> environment -> environment.stack()
                                .peek()
                                .pushOperand(value));

        compilers.put(NUMBER, numberAcceptor.get());

        compilers.put(SWITCH, new SwitchOperatorCompiler(this));

        compilers.put(EXPRESSION, expressionAcceptor.get());

        compilers.put(BOOLEAN_LITERAL, new BooleanLiteralCompiler());

        compilers.put(NUMERIC_EXPRESSION, new DetachedStackStatementCompiler(numericExpression.get()));

        compilers.put(BOOLEAN_EXPRESSION, new DetachedStackStatementCompiler(booleanExpression.get()));

        compilers.put(RELATIONAL_EXPRESSION, new RelationalExpressionCompiler(this));

        compilers.put(BOOLEAN_BRACKETS, new DetachedStackStatementCompiler(BracketsFSM.create(booleanExpression.get(),
                compilingExceptionThrower)));

        compilers.put(NUMERIC_BRACKETS, new DetachedStackStatementCompiler(BracketsFSM.create(numericExpression.get(),
                compilingExceptionThrower)));

        compilers.put(NUMERIC_OPERAND, new DetachedStackStatementCompiler(numericOperand.get()));

        compilers.put(BOOLEAN_OPERAND, new DetachedStackStatementCompiler(booleanOperand.get()));

        compilers.put(FUNCTION, new FunctionCompiler(this, new ValidatedFunctionFactoryImpl()));

        compilers.put(PROCEDURE, new ProcedureCompiler(this));

        compilers.put(VARIABLE_DECLARATION, new VariableDeclarationCompiler(this));

        compilers.put(VARIABLE_VALUE, new VariableValueCompiler());

        compilers.put(STRING_LITERAL, new StringLiteralCompiler());

        compilers.put(STRING_EXPRESSION, new DetachedStackStatementCompiler(stringExpression.get()));

        compilers.put(FOR, new ForLoopOperatorCompiler(this));

        compilers.put(UNARY_PREFIX_EXPRESSION, new UnaryPrefixExpressionCompiler(this, new UnaryOperatorFactory()));

        compilers.put(UNARY_POSTFIX_EXPRESSION, new UnaryPostfixExpressionCompiler(this, new UnaryOperatorFactory()));

        Supplier<TransitionOneOfMatrixBuilder<List<Command>, CompilingException>> readVariableAcceptor = () -> new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()
                .allowTransition(new CompileStatementAcceptor<List<Command>>(this, UNARY_PREFIX_EXPRESSION, List::add)
                        .named("UNARY PREFIX"))

                .allowTransition(new CompileStatementAcceptor<List<Command>>(this, UNARY_POSTFIX_EXPRESSION, List::add)
                        .named("UNARY POSTFIX"))

                .allowTransition(new CompileStatementAcceptor<List<Command>>(this, VARIABLE_VALUE, List::add)
                        .named("VARIABLE VALUE"));

        compilers.put(READ_VARIABLE, new DetachedStackStatementCompiler(
                FiniteStateMachine.oneOf("READ VARIABLE", readVariableAcceptor.get(), compilingExceptionThrower)));

        compilers.put(CONDITIONAL_OPERATOR, new ConditionalOperatorCompiler(this));
    }

    @Override
    public StatementCompiler create(StatementType type) {
        Preconditions.checkArgument(compilers.containsKey(type));
        return compilers.get(type);
    }
}