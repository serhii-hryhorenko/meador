package com.teamdev.meador.fsmimpl.conditional_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.compiler.StatementType;
import com.teamdev.meador.fsmimpl.util.CodeBlockFSM;
import com.teamdev.meador.fsmimpl.util.ConditionFSM;

public class IfOperatorFSM extends FiniteStateMachine<IfOperatorContext, CompilingException> {

    public static IfOperatorFSM create(StatementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var ifKeyword = new State.Builder<IfOperatorContext, CompilingException>()
                .setName("IF KEYWORD")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var optionalKeyword = TextIdentifierFSM.execute(inputSequence,
                            new ExceptionThrower<>(CompilingException::new));

                    return optionalKeyword.isPresent() && optionalKeyword.get().equals("if");
                })
                .setTemporary(true)
                .build();

        var condition = new State.Builder<IfOperatorContext, CompilingException>()
                .setName("IF CONDITION")
                .setAcceptor(ConditionFSM.create(factory, StatementType.BOOLEAN_EXPRESSION, IfOperatorContext::setCondition))
                .build();

        var ifCodeBlock = new State.Builder<IfOperatorContext, CompilingException>()
                .setName("IF CODE BLOCK")
                .setAcceptor(CodeBlockFSM.create(factory, IfOperatorContext::setStatements))
                .setFinite(true)
                .build();

        var matrix = TransitionMatrix.chainedTransitions(ifKeyword, condition, ifCodeBlock);

        return new IfOperatorFSM(matrix, new ExceptionThrower<>(CompilingException::new));
    }

    private IfOperatorFSM(TransitionMatrix<IfOperatorContext, CompilingException> transitionMatrix,
                          ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
