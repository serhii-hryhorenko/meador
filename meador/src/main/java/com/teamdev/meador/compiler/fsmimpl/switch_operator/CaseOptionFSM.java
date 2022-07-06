package com.teamdev.meador.compiler.fsmimpl.switch_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.compiler.StatementType;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchOptionContext;

public class CaseOptionFSM extends FiniteStateMachine<SwitchOptionContext, CompilingException> {

    public static CaseOptionFSM create(StatementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var keyword = new State.Builder<SwitchOptionContext, CompilingException>()
                .setName("OPTION KEYWORD")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var optionalWord = TextIdentifierFSM.execute(inputSequence, new ExceptionThrower<>(CompilingException::new));
                    return optionalWord.isPresent() && optionalWord.get().equals("case");
                })
                .build();

        var statement = new State.Builder<SwitchOptionContext, CompilingException>()
                .setName("OPTION VALUE")
                .setAcceptor(new CompileStatementAcceptor<>(factory, StatementType.EXPRESSION, SwitchOptionContext::setCondition))
                .build();

        var colon = new State.Builder<SwitchOptionContext, CompilingException>()
                .setName("COLON")
                .setAcceptor(StateAcceptor.acceptChar(':'))
                .build();

        var openCurlyBracket = new State.Builder<SwitchOptionContext, CompilingException>()
                .setName("OPEN CURLY BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('{'))
                .build();

        var executableExpression = new State.Builder<SwitchOptionContext, CompilingException>()
                .setName("EXECUTABLE EXPRESSION")
                .setAcceptor(new CompileStatementAcceptor<>(factory, StatementType.PROGRAM, SwitchOptionContext::setStatement))
                .build();

        var closeCurlyBracket = new State.Builder<SwitchOptionContext, CompilingException>()
                .setName("CLOSE CURLY BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('}'))
                .setFinite(true)
                .build();

        var matrix =
            TransitionMatrix.chainedTransitions(keyword, statement, colon, openCurlyBracket, executableExpression, closeCurlyBracket);


        return new CaseOptionFSM(matrix, new ExceptionThrower<>(CompilingException::new));
    }

    protected CaseOptionFSM(TransitionMatrix<SwitchOptionContext, CompilingException> transitionMatrix,
                            ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
