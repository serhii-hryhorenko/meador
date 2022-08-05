package com.teamdev.meador.fsmimpl.expression.relative;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.expression.OperatorAcceptor;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElement;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.runtime.operatorfactoryimpl.RelativeBinaryOperatorFactory;

/**
 * {@link FiniteStateMachine} implementation for recognizing relational expressions in Meador
 * programs.
 */
public class RelationalExpressionMachine extends FiniteStateMachine<RelationalExpressionOutputChain, CompilingException> {

    private RelationalExpressionMachine(
            TransitionMatrix<RelationalExpressionOutputChain, CompilingException> transitionMatrix,
            ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }

    public static RelationalExpressionMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var left = new State.Builder<RelationalExpressionOutputChain, CompilingException>()
                .setName("LEFT EXPRESSION")
                .setAcceptor(new CompileStatementAcceptor<>(factory,
                                                            ProgramElement.NUMERIC_EXPRESSION,
                                                            RelationalExpressionOutputChain::setLeft))
                .setTemporary()
                .build();

        var relationOperator = new State.Builder<RelationalExpressionOutputChain, CompilingException>()
                .setName("RELATION OPERATOR")
                .setAcceptor(new OperatorAcceptor<>(new RelativeBinaryOperatorFactory(),
                                                    RelationalExpressionOutputChain::setOperator))
                .build();

        var right = new State.Builder<RelationalExpressionOutputChain, CompilingException>()
                .setName("RIGHT EXPRESSION")
                .setAcceptor(new CompileStatementAcceptor<>(factory,
                                                            ProgramElement.NUMERIC_EXPRESSION,
                                                            RelationalExpressionOutputChain::setRight))
                .setFinal()
                .build();

        var matrix =
                TransitionMatrix.chainedTransitions(left, relationOperator, right);

        return new RelationalExpressionMachine(matrix,
                                               new ExceptionThrower<>(CompilingException::new));
    }
}
