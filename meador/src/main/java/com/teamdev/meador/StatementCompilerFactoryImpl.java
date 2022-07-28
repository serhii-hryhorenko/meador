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
import com.teamdev.meador.compiler.statement.boolean_expr.BooleanLiteralCompiler;
import com.teamdev.meador.compiler.statement.function.FunctionCompiler;
import com.teamdev.meador.compiler.statement.procedure.ProcedureCompiler;
import com.teamdev.meador.compiler.statement.relative_expr.RelationalExpressionCompiler;
import com.teamdev.meador.compiler.statement.string_expr.StringLiteralCompiler;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchOperatorCompiler;
import com.teamdev.meador.compiler.statement.variable.VariableDeclarationCompiler;
import com.teamdev.meador.compiler.statement.variable.VariableValueCompiler;
import com.teamdev.meador.fsmimpl.compiler.CompilerFSM;
import com.teamdev.meador.fsmimpl.util.DeepestParsedInputAcceptor;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.BooleanBinaryOperatorFactory;
import com.teamdev.runtime.value.MathBinaryOperatorFactoryImpl;
import com.teamdev.runtime.value.StringBinaryOperatorFactory;

import java.util.*;

import static com.teamdev.meador.compiler.StatementType.*;

public class StatementCompilerFactoryImpl implements StatementCompilerFactory {

    private final Map<StatementType, StatementCompiler> compilers = new EnumMap<>(
            StatementType.class);

    public StatementCompilerFactoryImpl() {
        compilers.put(PROGRAM, compilerInput -> {
            var commands = new ArrayList<Command>();

            if (CompilerFSM.create(this)
                    .accept(compilerInput, commands)) {

                return Optional.of(environment -> commands.forEach(command -> command.execute(environment)));
            }

            return Optional.empty();
        });

        compilers.put(SWITCH, new SwitchOperatorCompiler(this));

        compilers.put(NUMBER, inputSequence ->
                NumberFSM.execute(inputSequence, new ExceptionThrower<>(CompilingException::new))
                        .map(value -> environment -> environment.stack()
                                .peek()
                                .pushOperand(value))
        );

        compilers.put(EXPRESSION, new DetachedStackStatementCompiler(
                new DeepestParsedInputAcceptor<>(
                        ArrayList::new,

                        new CompileStatementAcceptor<List<Command>>(this, STRING_EXPRESSION, List::add)
                                .named("StringExpressionFSM"),

                        new CompileStatementAcceptor<List<Command>>(this, BOOLEAN_EXPRESSION, List::add)
                                .named("BooleanExpressionFSM"),

                        new CompileStatementAcceptor<List<Command>>(this, NUMERIC_EXPRESSION, List::add)
                                .named("NumericExpressionFSM")))
        );

        compilers.put(BOOLEAN_LITERAL, new BooleanLiteralCompiler());

        compilers.put(NUMERIC_EXPRESSION, new DetachedStackStatementCompiler(createNumericExpressionMachine()));

        compilers.put(BOOLEAN_EXPRESSION, new DetachedStackStatementCompiler(createBooleanExpressionMachine()));

        compilers.put(RELATIONAL_EXPRESSION, new RelationalExpressionCompiler(this));

        compilers.put(BOOLEAN_BRACKETS, new DetachedStackStatementCompiler(BracketsFSM.create(createBooleanExpressionMachine(),
                new ExceptionThrower<>(CompilingException::new))));

        compilers.put(NUMERIC_BRACKETS, new DetachedStackStatementCompiler(BracketsFSM.create(createNumericExpressionMachine(),
                new ExceptionThrower<>(CompilingException::new))));

        compilers.put(NUMERIC_OPERAND, new DetachedStackStatementCompiler(createNumericOperandMachine()));

        compilers.put(BOOLEAN_OPERAND, new DetachedStackStatementCompiler(createBooleanOperandMachine()));

        compilers.put(FUNCTION, new FunctionCompiler(this, new ValidatedFunctionFactoryImpl()));

        compilers.put(PROCEDURE, new ProcedureCompiler(this));

        compilers.put(VARIABLE_DECLARATION, new VariableDeclarationCompiler(this));

        compilers.put(VARIABLE_VALUE, new VariableValueCompiler());

        compilers.put(STRING_LITERAL, new StringLiteralCompiler());
    }

    private StateAcceptor<List<Command>, CompilingException> createNumericExpressionMachine() {
        return ExpressionFSM.create(
                new CompileStatementAcceptor<>(this, NUMERIC_OPERAND, List::add),
                new MathBinaryOperatorFactoryImpl(),
                (commands, operator) -> commands.add(environment -> environment.stack()
                        .peek()
                        .pushOperator(operator)),
                new ExceptionThrower<>(CompilingException::new)
        );
    }

    private StateAcceptor<List<Command>, CompilingException> createNumericOperandMachine() {
        return FiniteStateMachine.oneOf(
                "NumericOperandFSM",
                new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, NUMBER, List::add)
                                .named("MEADOR NUMBER"))

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, NUMERIC_BRACKETS, List::add)
                                .named("MEADOR BRACKETS"))

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, FUNCTION, List::add)
                                .named("MEADOR FUNCTION"))

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, VARIABLE_VALUE, List::add)
                                .named("MEADOR VARIABLE")),

                new ExceptionThrower<>(CompilingException::new)
        );
    }

    private StateAcceptor<List<Command>, CompilingException> createStringExpression() {
        return ExpressionFSM.create(
                createStringOperand(),
                new StringBinaryOperatorFactory(),

                (commands, operator) -> commands.add(environment -> environment.stack()
                        .peek()
                        .pushOperator(operator)),

                new ExceptionThrower<>(CompilingException::new)
        );
    }

    private StateAcceptor<List<Command>, CompilingException> createStringOperand() {
        return FiniteStateMachine.oneOf("STRING OPERAND",

                new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()
                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, STRING_LITERAL, List::add)
                                .named("STRING LITERAL"))

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, VARIABLE_VALUE, List::add)
                                .named("STRING VARIABLE")),

                new ExceptionThrower<>(CompilingException::new)
        );
    }

    private StateAcceptor<List<Command>, CompilingException> createBooleanExpressionMachine() {
        return ExpressionFSM.create(
                new CompileStatementAcceptor<>(this, BOOLEAN_OPERAND, List::add),

                new BooleanBinaryOperatorFactory(),

                (commands, operator) -> commands.add(environment -> environment.stack()
                        .peek()
                        .pushOperator(operator)),

                new ExceptionThrower<>(CompilingException::new)
        );
    }

    private StateAcceptor<List<Command>, CompilingException> createBooleanOperandMachine() {
        return FiniteStateMachine.oneOf(
                "BooleanOperandFSM",
                new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()
                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, BOOLEAN_LITERAL, List::add)
                                .named("MEADOR BOOLEAN LITERAL"), true)

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, BOOLEAN_BRACKETS, List::add)
                                .named("MEADOR BOOLEAN BRACKETS"))

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, RELATIONAL_EXPRESSION, List::add)
                                .named("MEADOR RELATIVE EXPRESSION"), true)

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, VARIABLE_VALUE, List::add)
                                .named("MEADOR VARIABLE")),

                new ExceptionThrower<>(CompilingException::new)
        );
    }

    @Override
    public StatementCompiler create(StatementType type) {
        Preconditions.checkArgument(compilers.containsKey(type));

        return compilers.get(type);
    }

}