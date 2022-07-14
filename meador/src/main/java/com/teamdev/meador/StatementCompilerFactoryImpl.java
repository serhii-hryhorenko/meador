package com.teamdev.meador;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionOneOfMatrixBuilder;
import com.teamdev.machine.brackets.BracketsFSM;
import com.teamdev.machine.expression.ExpressionFSM;
import com.teamdev.machine.number.NumberFSM;
import com.teamdev.machine.operand.OperandFSM;
import com.teamdev.machine.util.ValidatedFunctionFactoryImpl;
import com.teamdev.meador.compiler.*;
import com.teamdev.meador.compiler.statement.datastructure.DataStructureCompiler;
import com.teamdev.meador.compiler.statement.datastructure.DataStructureTemplateCompiler;
import com.teamdev.meador.compiler.statement.datastructure.FieldAssignmentCompiler;
import com.teamdev.meador.compiler.statement.datastructure.MemoryValueCompiler;
import com.teamdev.meador.compiler.statement.function.FunctionCompiler;
import com.teamdev.meador.compiler.statement.procedure.ProcedureCompiler;
import com.teamdev.meador.compiler.statement.relative_expr.RelationalExpressionCompiler;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchOperatorCompiler;
import com.teamdev.meador.compiler.statement.variable.VariableDeclarationCompiler;
import com.teamdev.meador.fsmimpl.compiler.CompilerFSM;
import com.teamdev.runtime.Command;
import com.teamdev.runtime.value.MathBinaryOperatorFactoryImpl;

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

        compilers.put(DATA_STRUCTURE_DECLARATION, new DataStructureTemplateCompiler(this));

        compilers.put(NUMBER, inputSequence ->
                NumberFSM.execute(inputSequence, new ExceptionThrower<>(CompilingException::new))
                        .map(value -> environment -> environment.stack()
                                .peek()
                                .pushOperand(value))
        );

        compilers.put(EXPRESSION, new DetachedStackStatementCompiler(
                OperandFSM.create(new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()
                                .allowTransition(new CompileStatementAcceptor<>(this, DATA_STRUCTURE_INSTANCE, List::add),
                                        "DATA STRUCTURE INSTANCE")
                                .allowTransition(new CompileStatementAcceptor<>(this, RELATIONAL_EXPRESSION, List::add),
                                        "RELATIONAL EXPRESSION", true)
                                .allowTransition(new CompileStatementAcceptor<>(this, NUMERIC_EXPRESSION, List::add),
                                        "NUMERIC EXPRESSION")
                                .build(),
                        new ExceptionThrower<>(CompilingException::new)
                )
        ));

        compilers.put(NUMERIC_EXPRESSION, new DetachedStackStatementCompiler(createNumericExpressionMachine()));

        compilers.put(RELATIONAL_EXPRESSION, new RelationalExpressionCompiler(this));

        compilers.put(DATA_STRUCTURE_INSTANCE, new DataStructureCompiler(this));

        compilers.put(BRACKETS, new DetachedStackStatementCompiler(BracketsFSM.create(createNumericExpressionMachine(),
                new ExceptionThrower<>(CompilingException::new))));

        compilers.put(OPERAND, new DetachedStackStatementCompiler(createOperandMachine()));

        compilers.put(FUNCTION, new FunctionCompiler(this, new ValidatedFunctionFactoryImpl()));

        compilers.put(PROCEDURE, new ProcedureCompiler(this));

        compilers.put(FIELD_ASSIGNMENT, new FieldAssignmentCompiler(this));

        compilers.put(MEMORY_VALUE, new MemoryValueCompiler());

        compilers.put(VARIABLE_DECLARATION, new VariableDeclarationCompiler(this));

//        compilers.put(VARIABLE_VALUE, new VariableValueCompiler());
    }

    private StateAcceptor<List<Command>, CompilingException> createNumericExpressionMachine() {
        return ExpressionFSM.create(
                new CompileStatementAcceptor<>(this, OPERAND, List::add),
                new MathBinaryOperatorFactoryImpl(),
                (commands, operator) -> commands.add(environment -> environment.stack()
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

                        .allowTransition(new CompileStatementAcceptor<>(this, MEMORY_VALUE, List::add),
                                "MEADOR MEMORY VALUE")

//                        .allowTransition(new CompileStatementAcceptor<>(this, FIELD_VALUE, List::add),
//                                "MEADOR FIELD VALUE")
//
//                        .allowTransition(new CompileStatementAcceptor<>(this, VARIABLE_VALUE, List::add),
//                                "MEADOR VARIABLE")
                        .build(),

                new ExceptionThrower<>(CompilingException::new)
        );
    }

    @Override
    public StatementCompiler create(StatementType type) {
        Preconditions.checkArgument(compilers.containsKey(type));

        return compilers.get(type);
    }
}