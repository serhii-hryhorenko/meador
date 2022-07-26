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
import com.teamdev.meador.compiler.statement.function.FunctionCompiler;
import com.teamdev.meador.compiler.statement.procedure.ProcedureCompiler;
import com.teamdev.meador.compiler.statement.relative_expr.RelationalExpressionCompiler;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchOperatorCompiler;
import com.teamdev.meador.compiler.statement.variable.VariableDeclarationCompiler;
import com.teamdev.meador.compiler.statement.variable.VariableValueCompiler;
import com.teamdev.meador.fsmimpl.compiler.CompilerFSM;
import com.teamdev.meador.fsmimpl.unary_operator.PostfixOperatorFSM;
import com.teamdev.meador.fsmimpl.unary_operator.PrefixOperatorFSM;
import com.teamdev.meador.fsmimpl.unary_operator.UnaryExpressionOutputChain;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.MathBinaryOperatorFactoryImpl;
import com.teamdev.runtime.value.UnaryOperatorFactoryImpl;

import java.util.*;

import static com.teamdev.meador.compiler.StatementType.*;

public class StatementCompilerFactoryImpl implements StatementCompilerFactory {

    private final Map<StatementType, StatementCompiler> compilers = new EnumMap<>(StatementType.class);

    public StatementCompilerFactoryImpl() {
        compilers.put(PROGRAM, compilerInput -> {
            var commands = new ArrayList<Command>();

            if (CompilerFSM.create(this)
                    .accept(compilerInput, commands)) {

                return Optional.of(environment -> commands.forEach(command -> command.execute(environment)));
            }

            return Optional.empty();
        });

        compilers.put(UNARY_PREFIX_EXPRESSION, inputSequence -> {
            UnaryExpressionOutputChain outputChain = new UnaryExpressionOutputChain();
            if (PrefixOperatorFSM.create(StatementCompilerFactoryImpl.this, new UnaryOperatorFactoryImpl())
                    .accept(inputSequence, outputChain)) {

                return Optional.of(runtimeEnvironment -> {
                    var variableCommand = new VariableValueCompiler().compile(outputChain.variableName());

                    variableCommand.ifPresent(command -> {
                        variableCommand.get().execute(runtimeEnvironment);

                        var topStack = runtimeEnvironment.stack().peek();

                        var applied = outputChain.unaryOperator().apply(topStack.popOperand());

                        topStack.pushOperand(applied);

                        if (outputChain.unaryOperator().mutates()) {
                            runtimeEnvironment.memory().putVariable(outputChain.variableName(), applied);
                        }
                    });
                });
            }

            return Optional.empty();
        });

        compilers.put(UNARY_POSTFIX_EXPRESSION, inputSequence -> {
            UnaryExpressionOutputChain outputChain = new UnaryExpressionOutputChain();
            if (PostfixOperatorFSM.create(StatementCompilerFactoryImpl.this, new UnaryOperatorFactoryImpl())
                    .accept(inputSequence, outputChain)) {
                return Optional.of(runtimeEnvironment -> {
                    var variableCommand = new VariableValueCompiler().compile(outputChain.variableName());

                    variableCommand.ifPresent(command -> {
                        variableCommand.get().execute(runtimeEnvironment);

                        runtimeEnvironment.memory().putVariable(outputChain.variableName(),
                                outputChain.unaryOperator().apply(runtimeEnvironment.stack().peek().peekOperand()));
                    });
                });
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
                FiniteStateMachine.oneOf(
                        "MeadorExpressionFSM",
                        new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()
                                .allowTransition(new CompileStatementAcceptor<List<Command>>(this, RELATIONAL_EXPRESSION, List::add)
                                        .named("RELATIONAL EXPRESSION"), true)

                                .allowTransition(new CompileStatementAcceptor<List<Command>>(this, NUMERIC_EXPRESSION, List::add)
                                        .named("NUMERIC EXPRESSION")),
                        new ExceptionThrower<>(CompilingException::new))
        ));

        compilers.put(NUMERIC_EXPRESSION, new DetachedStackStatementCompiler(createNumericExpressionMachine()));

        compilers.put(RELATIONAL_EXPRESSION, new RelationalExpressionCompiler(this));

        compilers.put(BRACKETS, new DetachedStackStatementCompiler(BracketsFSM.create(createNumericExpressionMachine(),
                new ExceptionThrower<>(CompilingException::new))));

        compilers.put(OPERAND, new DetachedStackStatementCompiler(createOperandMachine()));

        compilers.put(FUNCTION, new FunctionCompiler(this, new ValidatedFunctionFactoryImpl()));

        compilers.put(READ_VARIABLE, new DetachedStackStatementCompiler(FiniteStateMachine.oneOf("READ VARIABLE",
                new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()
                        .allowTransition(new CompileStatementAcceptor<List<Command>>(StatementCompilerFactoryImpl.this, UNARY_PREFIX_EXPRESSION, List::add)
                                .named("UNARY PREFIX"))

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(StatementCompilerFactoryImpl.this, UNARY_POSTFIX_EXPRESSION, List::add)
                                .named("UNARY POSTFIX"))

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(StatementCompilerFactoryImpl.this, VARIABLE_VALUE, List::add)
                                .named("VARIABLE VALUE")),
                new ExceptionThrower<>(CompilingException::new))));

        compilers.put(PROCEDURE, new ProcedureCompiler(this));

        compilers.put(VARIABLE_DECLARATION, new VariableDeclarationCompiler(this));

        compilers.put(VARIABLE_VALUE, new VariableValueCompiler());
    }

    private StateAcceptor<List<Command>, CompilingException> createNumericExpressionMachine() {
        return ExpressionFSM.create(
                new CompileStatementAcceptor<List<Command>>(this, OPERAND, List::add)
                        .named("NUMERIC OPERAND"),

                new MathBinaryOperatorFactoryImpl(),

                (commands, operator) -> commands.add(environment -> environment.stack()
                        .peek()
                        .pushOperator(operator)),

                new ExceptionThrower<>(CompilingException::new));
    }

    private StateAcceptor<List<Command>, CompilingException> createOperandMachine() {
        return FiniteStateMachine.oneOf(
                "NumericOperandFSM",
                new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, BRACKETS, List::add)
                                .named("MEADOR BRACKETS"))

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, FUNCTION, List::add)
                                .named("MEADOR FUNCTION"))

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, READ_VARIABLE, List::add)
                                .named("MEADOR VARIABLE"))

                        .allowTransition(new CompileStatementAcceptor<List<Command>>(this, NUMBER, List::add)
                                .named("MEADOR NUMBER")),


                new ExceptionThrower<>(CompilingException::new));
    }

    @Override
    public StatementCompiler create(StatementType type) {
        Preconditions.checkArgument(compilers.containsKey(type));

        return compilers.get(type);
    }
}