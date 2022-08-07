package com.teamdev.meador;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionOneOfMatrixBuilder;
import com.teamdev.machine.brackets.BracketsMachine;
import com.teamdev.machine.expression.ExpressionMachine;
import com.teamdev.machine.number.NumberMachine;
import com.teamdev.meador.compiler.util.CompilerMachine;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElement;
import com.teamdev.meador.compiler.ProgramElementCompiler;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.compiler.conditionaloperator.ConditionalOperatorCompiler;
import com.teamdev.meador.compiler.datastructure.DataStructureDeclarationCompiler;
import com.teamdev.meador.compiler.datastructure.DataStructureInstanceCompiler;
import com.teamdev.meador.compiler.datastructure.FieldAssignmentCompiler;
import com.teamdev.meador.compiler.datastructure.FieldValueCompiler;
import com.teamdev.meador.compiler.expression.bool.BooleanLiteralCompiler;
import com.teamdev.meador.compiler.expression.relative.RelationalExpressionCompiler;
import com.teamdev.meador.compiler.expression.string.StringLiteralCompiler;
import com.teamdev.meador.compiler.forloop.ForLoopOperatorCompiler;
import com.teamdev.meador.compiler.function.FunctionCompiler;
import com.teamdev.meador.compiler.procedure.ProcedureCompiler;
import com.teamdev.meador.compiler.switchoperator.SwitchOperatorCompiler;
import com.teamdev.meador.compiler.unaryoperator.UnaryPostfixExpressionCompiler;
import com.teamdev.meador.compiler.unaryoperator.UnaryPrefixExpressionCompiler;
import com.teamdev.meador.compiler.util.CompileStatementAcceptor;
import com.teamdev.meador.compiler.util.DeepestParsedInputAcceptor;
import com.teamdev.meador.compiler.util.DetachedStackStatementCompiler;
import com.teamdev.meador.compiler.variable.VariableAssignmentCompiler;
import com.teamdev.meador.compiler.variable.VariableValueCompiler;
import com.teamdev.meador.compiler.whileloop.WhileLoopCompiler;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.evaluation.operator.AbstractBinaryOperator;
import com.teamdev.runtime.functionfactoryimpl.ValidatedFunctionFactoryImpl;
import com.teamdev.runtime.operatorfactoryimpl.BooleanBinaryOperatorFactory;
import com.teamdev.runtime.operatorfactoryimpl.MathBinaryOperatorFactoryImpl;
import com.teamdev.runtime.operatorfactoryimpl.StringBinaryOperatorFactory;
import com.teamdev.runtime.operatorfactoryimpl.UnaryOperatorFactory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static com.teamdev.meador.compiler.ProgramElement.BOOLEAN_BRACKETS;
import static com.teamdev.meador.compiler.ProgramElement.BOOLEAN_EXPRESSION;
import static com.teamdev.meador.compiler.ProgramElement.BOOLEAN_LITERAL;
import static com.teamdev.meador.compiler.ProgramElement.BOOLEAN_OPERAND;
import static com.teamdev.meador.compiler.ProgramElement.CONDITIONAL_OPERATOR;
import static com.teamdev.meador.compiler.ProgramElement.DATA_STRUCTURE_DECLARATION;
import static com.teamdev.meador.compiler.ProgramElement.DATA_STRUCTURE_INSTANCE;
import static com.teamdev.meador.compiler.ProgramElement.EXPRESSION;
import static com.teamdev.meador.compiler.ProgramElement.FOR_LOOP;
import static com.teamdev.meador.compiler.ProgramElement.FUNCTION;
import static com.teamdev.meador.compiler.ProgramElement.LIST_OF_STATEMENTS;
import static com.teamdev.meador.compiler.ProgramElement.NUMBER;
import static com.teamdev.meador.compiler.ProgramElement.NUMERIC_BRACKETS;
import static com.teamdev.meador.compiler.ProgramElement.NUMERIC_EXPRESSION;
import static com.teamdev.meador.compiler.ProgramElement.NUMERIC_OPERAND;
import static com.teamdev.meador.compiler.ProgramElement.PROCEDURE;
import static com.teamdev.meador.compiler.ProgramElement.READ_VARIABLE;
import static com.teamdev.meador.compiler.ProgramElement.RELATIONAL_EXPRESSION;
import static com.teamdev.meador.compiler.ProgramElement.STRING_EXPRESSION;
import static com.teamdev.meador.compiler.ProgramElement.STRING_LITERAL;
import static com.teamdev.meador.compiler.ProgramElement.STRUCTURE_FIELD_ASSIGNMENT;
import static com.teamdev.meador.compiler.ProgramElement.STRUCTURE_FIELD_VALUE;
import static com.teamdev.meador.compiler.ProgramElement.SWITCH_OPERATOR;
import static com.teamdev.meador.compiler.ProgramElement.UNARY_POSTFIX_EXPRESSION;
import static com.teamdev.meador.compiler.ProgramElement.UNARY_PREFIX_EXPRESSION;
import static com.teamdev.meador.compiler.ProgramElement.VARIABLE_ASSIGNMENT;
import static com.teamdev.meador.compiler.ProgramElement.VARIABLE_VALUE;
import static com.teamdev.meador.compiler.ProgramElement.WHILE_LOOP;

/**
 * {@link ProgramElementCompilerFactoryImpl} which provides creation of any implemented Meador
 * program element.
 */
public class ProgramElementCompilerFactoryImpl implements ProgramElementCompilerFactory {

    private final Map<ProgramElement, ProgramElementCompiler> compilers = new EnumMap<>(ProgramElement.class);

    public ProgramElementCompilerFactoryImpl() {
        var compilingExceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var numericOperand = (Supplier<StateAcceptor<List<Command>, CompilingException>>) () -> FiniteStateMachine.oneOf(
                "NUMERIC OPERAND",
                new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()

                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(this, NUMERIC_BRACKETS,
                                                                            List::add)
                                        .named("MEADOR BRACKETS"))

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, FUNCTION,
                                                                                     List::add)
                                                 .named("MEADOR FUNCTION"))

                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(this, READ_VARIABLE,
                                                                            List::add)
                                        .named("MEADOR VARIABLE"))

                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(this, NUMBER, List::add)
                                        .named("MEADOR NUMBER")),

                compilingExceptionThrower
        );

        var booleanOperand = (Supplier<StateAcceptor<List<Command>, CompilingException>>) () -> FiniteStateMachine.oneOf(
                "BOOLEAN OPERAND",
                new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()
                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(this, BOOLEAN_LITERAL,
                                                                            List::add)
                                        .named("MEADOR BOOLEAN LITERAL"), true)

                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(this, BOOLEAN_BRACKETS,
                                                                            List::add)
                                        .named("MEADOR BOOLEAN BRACKETS"))

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this,
                                                                                     RELATIONAL_EXPRESSION,
                                                                                     List::add)
                                                 .named("MEADOR RELATIVE EXPRESSION"), true)

                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(this, READ_VARIABLE,
                                                                            List::add)
                                        .named("MEADOR VARIABLE")),

                compilingExceptionThrower
        );

        var stringOperand = (Supplier<StateAcceptor<List<Command>, CompilingException>>) () -> FiniteStateMachine.oneOf(
                "STRING OPERAND",
                new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()
                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(this, STRING_LITERAL,
                                                                            List::add)
                                        .named("STRING LITERAL"))

                        .allowTransition(new DeepestParsedInputAcceptor<>(
                                ArrayList::new,
                                booleanOperand.get(),
                                numericOperand.get())),

                compilingExceptionThrower
        );

        var pushBinaryOperator = (BiConsumer<List<Command>, AbstractBinaryOperator>) (commands, operator) ->
                commands.add(environment -> environment.stack()
                                                       .peek()
                                                       .pushOperator(operator));

        var numericExpression = (Supplier<ExpressionMachine<List<Command>, CompilingException>>) () -> ExpressionMachine.create(
                numericOperand.get(),
                new MathBinaryOperatorFactoryImpl(),
                pushBinaryOperator,
                compilingExceptionThrower
        );

        var booleanExpression = (Supplier<ExpressionMachine<List<Command>, CompilingException>>) () -> ExpressionMachine.create(
                booleanOperand.get(),
                new BooleanBinaryOperatorFactory(),
                pushBinaryOperator,
                compilingExceptionThrower
        );

        var stringExpression = (Supplier<ExpressionMachine<List<Command>, CompilingException>>) () -> ExpressionMachine.create(
                stringOperand.get(),
                new StringBinaryOperatorFactory(),
                pushBinaryOperator,
                compilingExceptionThrower
        );

        var expressionAcceptor = (Supplier<DetachedStackStatementCompiler>) () -> new DetachedStackStatementCompiler(
                new DeepestParsedInputAcceptor<>(
                        ArrayList::new,

                        new CompileStatementAcceptor<List<Command>>(this, DATA_STRUCTURE_INSTANCE,
                                                                    List::add)
                                .named("DATA STRUCTURE INSTANCE"),

                        new CompileStatementAcceptor<List<Command>>(this, BOOLEAN_EXPRESSION,
                                                                    List::add)
                                .named("BOOLEAN EXPRESSION"),

                        new CompileStatementAcceptor<List<Command>>(this, NUMERIC_EXPRESSION,
                                                                    List::add)
                                .named("NUMERIC EXPRESSION"),

                        new CompileStatementAcceptor<List<Command>>(this, STRING_EXPRESSION,
                                                                    List::add)
                                .named("STRING EXPRESSION")
                )
        );

        var programAcceptor = (Supplier<ProgramElementCompiler>) () -> reader -> {
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

        var numberAcceptor = (Supplier<ProgramElementCompiler>) () -> inputSequence ->
                NumberMachine.execute(inputSequence, compilingExceptionThrower)
                             .map(value -> environment -> environment.stack()
                                                                     .peek()
                                                                     .pushOperand(value));

        compilers.put(NUMBER, numberAcceptor.get());

        compilers.put(SWITCH_OPERATOR, new SwitchOperatorCompiler(this));

        compilers.put(EXPRESSION, expressionAcceptor.get());

        compilers.put(BOOLEAN_LITERAL, new BooleanLiteralCompiler());

        compilers.put(NUMERIC_EXPRESSION,
                      new DetachedStackStatementCompiler(numericExpression.get()));

        compilers.put(BOOLEAN_EXPRESSION,
                      new DetachedStackStatementCompiler(booleanExpression.get()));

        compilers.put(RELATIONAL_EXPRESSION, new RelationalExpressionCompiler(this));

        compilers.put(BOOLEAN_BRACKETS, new DetachedStackStatementCompiler(
                BracketsMachine.create(booleanExpression.get(),
                                       compilingExceptionThrower)));

        compilers.put(NUMERIC_BRACKETS, new DetachedStackStatementCompiler(
                BracketsMachine.create(numericExpression.get(),
                                       compilingExceptionThrower)));

        compilers.put(NUMERIC_OPERAND, new DetachedStackStatementCompiler(numericOperand.get()));

        compilers.put(BOOLEAN_OPERAND, new DetachedStackStatementCompiler(booleanOperand.get()));

        compilers.put(FUNCTION, new FunctionCompiler(this, new ValidatedFunctionFactoryImpl()));

        compilers.put(PROCEDURE, new ProcedureCompiler(this));

        compilers.put(VARIABLE_ASSIGNMENT, new VariableAssignmentCompiler(this));

        compilers.put(VARIABLE_VALUE, new VariableValueCompiler());

        compilers.put(STRING_LITERAL, new StringLiteralCompiler());

        compilers.put(STRING_EXPRESSION,
                      new DetachedStackStatementCompiler(stringExpression.get()));

        compilers.put(FOR_LOOP, new ForLoopOperatorCompiler(this));

        compilers.put(WHILE_LOOP, new WhileLoopCompiler(this));

        compilers.put(UNARY_PREFIX_EXPRESSION,
                      new UnaryPrefixExpressionCompiler(this, new UnaryOperatorFactory()));

        compilers.put(UNARY_POSTFIX_EXPRESSION,
                      new UnaryPostfixExpressionCompiler(this, new UnaryOperatorFactory()));

        var readVariableAcceptor = (Supplier<StateAcceptor<List<Command>, CompilingException>>) () ->
                FiniteStateMachine.oneOf("VALUE FROM MEMORY",

                                         new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()
                                                 .allowTransition(
                                                         new CompileStatementAcceptor<List<Command>>(
                                                                 this, UNARY_PREFIX_EXPRESSION,
                                                                 List::add)
                                                                 .named("UNARY PREFIX"))

                                                 .allowTransition(
                                                         new CompileStatementAcceptor<List<Command>>(
                                                                 this, UNARY_POSTFIX_EXPRESSION,
                                                                 List::add)
                                                                 .named("UNARY POSTFIX"))

                                                 .allowTransition(FiniteStateMachine.oneOf(
                                                         "VALUE STORED IN MEMORY",
                                                         new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()
                                                                 .allowTransition(
                                                                         new CompileStatementAcceptor<List<Command>>(
                                                                                 this,
                                                                                 STRUCTURE_FIELD_VALUE,
                                                                                 List::add)
                                                                                 .named("STRUCTURE FIELD VALUE"))

                                                                 .allowTransition(
                                                                         new CompileStatementAcceptor<List<Command>>(
                                                                                 this,
                                                                                 VARIABLE_VALUE,
                                                                                 List::add)
                                                                                 .named("VARIABLE VALUE")),

                                                         compilingExceptionThrower)),

                                         compilingExceptionThrower);

        compilers.put(READ_VARIABLE,
                      new DetachedStackStatementCompiler(readVariableAcceptor.get()));

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