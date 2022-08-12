package com.teamdev.meador.programelement.expression.fsmimpl.relative;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.expression.OperatorAcceptor;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElement;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.util.CompileStatementAcceptor;
import com.teamdev.runtime.operatorfactoryimpl.RelativeBinaryOperatorFactory;

/**
 * {@link FiniteStateMachine} implementation for recognizing relational expressions in Meador
 * programs.
 */
public class RelationalExpressionMachine extends FiniteStateMachine<RelationalExpressionOutputChain, SyntaxException> {

    private RelationalExpressionMachine(
            TransitionMatrix<RelationalExpressionOutputChain, SyntaxException> transitionMatrix,
            ExceptionThrower<SyntaxException> thrower) {
        super(transitionMatrix, thrower);
    }

    public static RelationalExpressionMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var left = new State.Builder<RelationalExpressionOutputChain, SyntaxException>()
                .setName("LEFT EXPRESSION")
                .setAcceptor(new CompileStatementAcceptor<>(factory,
                                                            ProgramElement.NUMERIC_EXPRESSION,
                                                            RelationalExpressionOutputChain::setLeft))
                .setTemporary()
                .build();

        var relationOperator = new State.Builder<RelationalExpressionOutputChain, SyntaxException>()
                .setName("RELATION OPERATOR")
                .setAcceptor(new OperatorAcceptor<>(new RelativeBinaryOperatorFactory(),
                                                    RelationalExpressionOutputChain::setOperator))
                .build();

        var right = new State.Builder<RelationalExpressionOutputChain, SyntaxException>()
                .setName("RIGHT EXPRESSION")
                .setAcceptor(new CompileStatementAcceptor<>(factory,
                                                            ProgramElement.NUMERIC_EXPRESSION,
                                                            RelationalExpressionOutputChain::setRight))
                .setFinal()
                .build();

        var matrix =
                TransitionMatrix.chainedTransitions(left, relationOperator, right);

        return new RelationalExpressionMachine(matrix,
                                               new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize a relational expression.")));
    }
}
