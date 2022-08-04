package com.teamdev.meador;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionOneOfMatrixBuilder;
import com.teamdev.machine.brackets.BracketsMachine;
import com.teamdev.machine.expression.ExpressionMachine;
import com.teamdev.machine.number.NumberMachine;
import com.teamdev.machine.util.ValidatedFunctionFactoryImpl;
import com.teamdev.meador.compiler.*;
import com.teamdev.meador.compiler.statement.conditional_operator.ConditionalOperatorCompiler;
import com.teamdev.meador.compiler.statement.datastructure.DataStructureDeclarationCompiler;
import com.teamdev.meador.compiler.statement.datastructure.DataStructureInstanceCompiler;
import com.teamdev.meador.compiler.statement.datastructure.FieldAssignmentCompiler;
import com.teamdev.meador.compiler.statement.datastructure.FieldValueCompiler;
import com.teamdev.meador.compiler.statement.expression.bool.BooleanLiteralCompiler;
import com.teamdev.meador.compiler.statement.expression.relative.RelationalExpressionCompiler;
import com.teamdev.meador.compiler.statement.expression.string.StringLiteralCompiler;
import com.teamdev.meador.compiler.statement.for_loop.ForLoopOperatorCompiler;
import com.teamdev.meador.compiler.statement.function.FunctionCompiler;
import com.teamdev.meador.compiler.statement.procedure.ProcedureCompiler;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchOperatorCompiler;
import com.teamdev.meador.compiler.statement.unary_operator.UnaryPostfixExpressionCompiler;
import com.teamdev.meador.compiler.statement.unary_operator.UnaryPrefixExpressionCompiler;
import com.teamdev.meador.compiler.statement.variable.VariableAssignmentCompiler;
import com.teamdev.meador.compiler.statement.variable.VariableValueCompiler;
import com.teamdev.meador.fsmimpl.compiler.CompilerMachine;
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

import static com.teamdev.meador.compiler.ProgramElement.*;

/**
 * {@link ProgramElementCompilerFactoryImpl} which provides creation of any implemented Meador program element.
 */
public class ProgramElementCompilerFactoryImpl implements ProgramElementCompilerFactory {

    private final Map<ProgramElement, ProgramElementCompiler> compilers = new EnumMap<>(ProgramElement.class);

    public ProgramElementCompilerFactoryImpl() {
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

        Supplier<ExpressionMachine<List<Command>, CompilingException>> numericExpression = () -> ExpressionMachine.create(
                numericOperand.get(),
                new MathBinaryOperatorFactoryImpl(),
                pushBinaryOperator,
                compilingExceptionThrower
        );

        Supplier<ExpressionMachine<List<Command>, CompilingException>> booleanExpression = () -> ExpressionMachine.create(
                booleanOperand.get(),
                new BooleanBinaryOperatorFactory(),
                pushBinaryOperator,
                compilingExceptionThrower
        );

        Supplier<ExpressionMachine<List<Command>, CompilingException>> stringExpression = () -> ExpressionMachine.create(
                stringOperand.get(),
                new StringBinaryOperatorFactory(),
                pushBinaryOperator,
                compilingExceptionThrower
        );

        Supplier<DetachedStackStatementCompiler> expressionAcceptor = () -> new DetachedStackStatementCompiler(
                new DeepestParsedInputAcceptor<>(
                        ArrayList::new,

                        new CompileStatementAcceptor<List<Command>>(this, DATA_STRUCTURE_INSTANCE, List::add)
                                .named("DATA STRUCTURE INSTANCE"),

                        new CompileStatementAcceptor<List<Command>>(this, BOOLEAN_EXPRESSION, List::add)
                                .named("BOOLEAN EXPRESSION"),

                        new CompileStatementAcceptor<List<Command>>(this, NUMERIC_EXPRESSION, List::add)
                                .named("NUMERIC EXPRESSION"),

                        new CompileStatementAcceptor<List<Command>>(this, STRING_EXPRESSION, List::add)
                                .named("STRING EXPRESSION")
                )
        );

        Supplier<ProgramElementCompiler> programAcceptor = () -> reader -> {
            var commands = new ArrayList<Command>();

            while (reader.canRead() && CompilerMachine.create(this).accept(reader, commands));

            if (commands.isEmpty()) {
                throw new CompilingException("List of statements is empty.");
            }

            return Optional.of(runtimeEnvironment -> {
                for (var command : commands) {
                    command.execute(runtimeEnvironment);
                }
            });
        };

        compilers.put(LIST_OF_STATEMENTS, programAcceptor.get());

        Supplier<ProgramElementCompiler> numberAcceptor = () -> inputSequence ->
                NumberMachine.execute(inputSequence, compilingExceptionThrower)
                        .map(value -> environment -> environment.stack()
                                .peek()
                                .pushOperand(value));

        compilers.put(NUMBER, numberAcceptor.get());

        compilers.put(SWITCH_OPERATOR, new SwitchOperatorCompiler(this));

        compilers.put(EXPRESSION, expressionAcceptor.get());

        compilers.put(BOOLEAN_LITERAL, new BooleanLiteralCompiler());

        compilers.put(NUMERIC_EXPRESSION, new DetachedStackStatementCompiler(numericExpression.get()));

        compilers.put(BOOLEAN_EXPRESSION, new DetachedStackStatementCompiler(booleanExpression.get()));

        compilers.put(RELATIONAL_EXPRESSION, new RelationalExpressionCompiler(this));

        compilers.put(BOOLEAN_BRACKETS, new DetachedStackStatementCompiler(BracketsMachine.create(booleanExpression.get(),
                compilingExceptionThrower)));

        compilers.put(NUMERIC_BRACKETS, new DetachedStackStatementCompiler(BracketsMachine.create(numericExpression.get(),
                compilingExceptionThrower)));

        compilers.put(NUMERIC_OPERAND, new DetachedStackStatementCompiler(numericOperand.get()));

        compilers.put(BOOLEAN_OPERAND, new DetachedStackStatementCompiler(booleanOperand.get()));

        compilers.put(FUNCTION, new FunctionCompiler(this, new ValidatedFunctionFactoryImpl()));

        compilers.put(PROCEDURE, new ProcedureCompiler(this));

        compilers.put(VARIABLE_ASSIGNMENT, new VariableAssignmentCompiler(this));

        compilers.put(VARIABLE_VALUE, new VariableValueCompiler());

        compilers.put(STRING_LITERAL, new StringLiteralCompiler());

        compilers.put(STRING_EXPRESSION, new DetachedStackStatementCompiler(stringExpression.get()));

        compilers.put(FOR_LOOP, new ForLoopOperatorCompiler(this));

        compilers.put(UNARY_PREFIX_EXPRESSION, new UnaryPrefixExpressionCompiler(this, new UnaryOperatorFactory()));

        compilers.put(UNARY_POSTFIX_EXPRESSION, new UnaryPostfixExpressionCompiler(this, new UnaryOperatorFactory()));

        Supplier<StateAcceptor<List<Command>, CompilingException>> readVariableAcceptor = () ->
                FiniteStateMachine.oneOf("VALUE FROM MEMORY",

                new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()
                    .allowTransition(new CompileStatementAcceptor<List<Command>>(this, UNARY_PREFIX_EXPRESSION, List::add)
                            .named("UNARY PREFIX"))

                    .allowTransition(new CompileStatementAcceptor<List<Command>>(this, UNARY_POSTFIX_EXPRESSION, List::add)
                            .named("UNARY POSTFIX"))

                    .allowTransition(FiniteStateMachine.oneOf("VALUE STORED IN MEMORY",
                            new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()
                                    .allowTransition(new CompileStatementAcceptor<List<Command>>(this, STRUCTURE_FIELD_VALUE, List::add)
                                        .named("STRUCTURE FIELD VALUE"))

                                    .allowTransition(new CompileStatementAcceptor<List<Command>>(this, VARIABLE_VALUE, List::add)
                                        .named("VARIABLE VALUE")),

                                compilingExceptionThrower)),

                    compilingExceptionThrower);


        compilers.put(READ_VARIABLE, new DetachedStackStatementCompiler(readVariableAcceptor.get()));

        compilers.put(CONDITIONAL_OPERATOR, new ConditionalOperatorCompiler(this));

        compilers.put(STRUCTURE_FIELD_VALUE, new FieldValueCompiler());

        compilers.put(STRUCTURE_FIELD_ASSIGNMENT, new FieldAssignmentCompiler(this));

        compilers.put(DATA_STRUCTURE_DECLARATION, new DataStructureDeclarationCompiler(this));

        compilers.put(DATA_STRUCTURE_INSTANCE, new DataStructureInstanceCompiler(this));
    }

    @Override
    public ProgramElementCompiler create(ProgramElement type) {
        Preconditions.checkArgument(compilers.containsKey(type));
        return compilers.get(type);
    }
}