package com.teamdev.meador;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionOneOfMatrixBuilder;
import com.teamdev.machine.brackets.BracketsMachine;
import com.teamdev.machine.expression.ExpressionMachine;
import com.teamdev.machine.number.NumberMachine;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElement;
import com.teamdev.meador.programelement.ProgramElementCompiler;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.conditionaloperator.ConditionalOperatorCompiler;
import com.teamdev.meador.programelement.datastructure.DataStructureDeclarationCompiler;
import com.teamdev.meador.programelement.datastructure.DataStructureInstanceCompiler;
import com.teamdev.meador.programelement.datastructure.FieldAssignmentCompiler;
import com.teamdev.meador.programelement.datastructure.FieldValueCompiler;
import com.teamdev.meador.programelement.expression.bool.BooleanLiteralCompiler;
import com.teamdev.meador.programelement.expression.relative.RelationalExpressionCompiler;
import com.teamdev.meador.programelement.expression.string.StringLiteralCompiler;
import com.teamdev.meador.programelement.forloop.ForLoopOperatorCompiler;
import com.teamdev.meador.programelement.function.FunctionCompiler;
import com.teamdev.meador.programelement.procedure.ProcedureCompiler;
import com.teamdev.meador.programelement.switchoperator.SwitchOperatorCompiler;
import com.teamdev.meador.programelement.unaryoperator.UnaryPostfixExpressionCompiler;
import com.teamdev.meador.programelement.unaryoperator.UnaryPrefixExpressionCompiler;
import com.teamdev.meador.programelement.util.CompileStatementAcceptor;
import com.teamdev.meador.programelement.util.CompilerMachine;
import com.teamdev.meador.programelement.util.DeepestParsedInputAcceptor;
import com.teamdev.meador.programelement.util.DetachedStackStatementCompiler;
import com.teamdev.meador.programelement.variable.VariableAssignmentCompiler;
import com.teamdev.meador.programelement.variable.VariableValueCompiler;
import com.teamdev.meador.programelement.whileloop.WhileLoopCompiler;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.evaluation.operator.AbstractBinaryOperator;
import com.teamdev.runtime.functionfactoryimpl.ValidatedFunctionFactoryImpl;
import com.teamdev.runtime.operatorfactoryimpl.BooleanBinaryOperatorFactory;
import com.teamdev.runtime.operatorfactoryimpl.MathBinaryOperatorFactoryImpl;
import com.teamdev.runtime.operatorfactoryimpl.StringBinaryOperatorFactory;
import com.teamdev.runtime.operatorfactoryimpl.UnaryOperatorFactory;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static com.teamdev.meador.programelement.ProgramElement.*;

/**
 * {@link ProgramElementCompilerFactory} implementation which provides creation of any implemented
 * Meador program element.
 */
public class ProgramElementCompilerFactoryImpl implements ProgramElementCompilerFactory {

    private final Map<ProgramElement, ProgramElementCompiler> compilers = new EnumMap<>(
            ProgramElement.class);

    public ProgramElementCompilerFactoryImpl() {
        var numericOperand = (Supplier<StateAcceptor<List<Command>, SyntaxException>>) () -> FiniteStateMachine.oneOf(
                "NumericOperandMachine",
                new TransitionOneOfMatrixBuilder<List<Command>, SyntaxException>()

                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(this, NUMERIC_BRACKETS,
                                        List::add)
                                        .named("MEADOR BRACKETS"))

                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(this, FUNCTION,
                                        List::add)
                                        .named("MEADOR FUNCTION"))

                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(this, READ_VARIABLE,
                                        List::add)
                                        .named("MEADOR VARIABLE"))

                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(this, NUMBER, List::add)
                                        .named("MEADOR NUMBER")),

                new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize numeric operand."))
        );

        var booleanOperand = (Supplier<StateAcceptor<List<Command>, SyntaxException>>) () -> FiniteStateMachine.oneOf(
                "BooleanOperandMachine",
                new TransitionOneOfMatrixBuilder<List<Command>, SyntaxException>()
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

                new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize boolean operand."))

        );

        var stringOperand = (Supplier<StateAcceptor<List<Command>, SyntaxException>>) () -> FiniteStateMachine.oneOf(
                "StringOperandMachine",
                new TransitionOneOfMatrixBuilder<List<Command>, SyntaxException>()
                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(this, STRING_LITERAL,
                                        List::add)
                                        .named("STRING LITERAL"))

                        .allowTransition(new DeepestParsedInputAcceptor<>(
                                ArrayList::new,
                                booleanOperand.get(),
                                numericOperand.get())),

                new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize string operand."))
        );

        var pushBinaryOperator = (BiConsumer<List<Command>, AbstractBinaryOperator>) (commands, operator) ->
                commands.add(environment -> environment.stack()
                        .peek()
                        .pushOperator(operator));

        var numericExpression = (Supplier<ExpressionMachine<List<Command>, SyntaxException>>) () -> ExpressionMachine.create(
                numericOperand.get(),
                new MathBinaryOperatorFactoryImpl(),
                pushBinaryOperator,
                new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize numeric expression."))
        );

        var booleanExpression = (Supplier<ExpressionMachine<List<Command>, SyntaxException>>) () -> ExpressionMachine.create(
                booleanOperand.get(),
                new BooleanBinaryOperatorFactory(),
                pushBinaryOperator,
                new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize boolean expression."))
        );

        var stringExpression = (Supplier<ExpressionMachine<List<Command>, SyntaxException>>) () -> ExpressionMachine.create(
                stringOperand.get(),
                new StringBinaryOperatorFactory(),
                pushBinaryOperator,
                new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize string expression."))
        );

        var expressionAcceptor = (Supplier<DetachedStackStatementCompiler>) () -> new DetachedStackStatementCompiler(
                new DeepestParsedInputAcceptor<>(
                        ArrayList::new,

                        new CompileStatementAcceptor<List<Command>>(this, DATA_STRUCTURE_INSTANCE,
                                List::add)
                                .named("DataStructureInstanceMachine"),

                        new CompileStatementAcceptor<List<Command>>(this, BOOLEAN_EXPRESSION,
                                List::add)
                                .named("BooleanExpressionMachine"),

                        new CompileStatementAcceptor<List<Command>>(this, NUMERIC_EXPRESSION,
                                List::add)
                                .named("NumericExpressionMachine"),

                        new CompileStatementAcceptor<List<Command>>(this, STRING_EXPRESSION,
                                List::add)
                                .named("StringExpressionMachine")
                )
        );

        var programAcceptor = (Supplier<ProgramElementCompiler>) () -> reader -> {
            var commands = new ArrayList<Command>();

            while (reader.canRead() && CompilerMachine.create(this)
                    .accept(reader, commands)) ;

            if (commands.isEmpty()) {
                throw new SyntaxException("Program does not contain any of statements.");
            }

            return Optional.of(runtimeEnvironment -> {
                for (var command : commands) {
                    command.execute(runtimeEnvironment);
                }
            });
        };

        compilers.put(LIST_OF_STATEMENTS, programAcceptor.get());

        var numberAcceptor = (Supplier<ProgramElementCompiler>) () -> inputSequence ->
                NumberMachine.execute(inputSequence, new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize a number.")))
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
                        new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize boolean expression in brackets.")))));

        compilers.put(NUMERIC_BRACKETS, new DetachedStackStatementCompiler(
                BracketsMachine.create(numericExpression.get(),
                        new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize numeric expression in brackets.")))));

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

        var unaryExpression = (Supplier<StateAcceptor<List<Command>, SyntaxException>>) () ->
                FiniteStateMachine.oneOf(
                        "StoredValueMachine",

                        new TransitionOneOfMatrixBuilder<List<Command>, SyntaxException>()
                                .allowTransition(
                                        new CompileStatementAcceptor<List<Command>>(
                                                this, UNARY_PREFIX_EXPRESSION, List::add)
                                                .named("UNARY PREFIX"))

                                .allowTransition(
                                        new CompileStatementAcceptor<List<Command>>(
                                                this, UNARY_POSTFIX_EXPRESSION,
                                                List::add)
                                                .named("UNARY POSTFIX"))

                                .allowTransition(FiniteStateMachine.oneOf(
                                        "STORED VALUE",
                                        new TransitionOneOfMatrixBuilder<List<Command>, SyntaxException>()
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

                                        new ExceptionThrower<>(() -> new SyntaxException("Failed to read variable or structure field.")))),

                        new ExceptionThrower<>(() -> new SyntaxException("Failed to read unary expression.")));

        compilers.put(READ_VARIABLE, new DetachedStackStatementCompiler(unaryExpression.get()));

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