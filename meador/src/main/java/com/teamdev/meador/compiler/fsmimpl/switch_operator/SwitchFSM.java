package com.teamdev.meador.compiler.fsmimpl.switch_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.compiler.StatementType;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchContext;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchOptionContext;

public class SwitchFSM extends FiniteStateMachine<SwitchContext, CompilingException> {

    public static SwitchFSM create(StatementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var initial = State.<SwitchContext, CompilingException>initialState();

        var switchKeyword = new State.Builder<SwitchContext, CompilingException>()
                .setName("SWITCH")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var optionalKeyword = TextIdentifierFSM.execute(inputSequence,
                            new ExceptionThrower<>(CompilingException::new));

                    return optionalKeyword.isPresent() && optionalKeyword.get().equals("switch");
                })
                .build();

        var openBracket = new State.Builder<SwitchContext, CompilingException>()
                .setName("OPEN BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('('))
                .build();

        var expressionToMatch = new State.Builder<SwitchContext, CompilingException>()
                .setName("EXPRESSION TO MATCH")
                .setAcceptor(new CompileStatementAcceptor<>(factory, StatementType.NUMERIC_EXPRESSION, SwitchContext::setValue))
                .build();

        var closeBracket = new State.Builder<SwitchContext, CompilingException>()
                .setName("CLOSE BRACKET")
                .setAcceptor(StateAcceptor.acceptChar(')'))
                .build();

        var openCurlyBracket = new State.Builder<SwitchContext, CompilingException>()
                .setName("OPEN CURLY BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('{'))
                .build();

        var caseOption = new State.Builder<SwitchContext, CompilingException>()
                .setName("CASE OPTION")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var context = new SwitchOptionContext();

                    if (CaseOptionFSM.create(factory).accept(inputSequence, context)) {
                        outputSequence.addOption(context);
                        return true;
                    }

                    return false;
                })
                .build();

        var closeCurlyBracket = new State.Builder<SwitchContext, CompilingException>()
                .setName("CLOSE CURLY BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('}'))
                .setFinite(true)
                .build();

        var matrix = new TransitionMatrixBuilder<SwitchContext, CompilingException>()
                .withStartState(initial)
                .allowTransition(initial, switchKeyword)
                .allowTransition(switchKeyword, openBracket)
                .allowTransition(openBracket, expressionToMatch)
                .allowTransition(expressionToMatch, closeBracket)
                .allowTransition(closeBracket, openCurlyBracket)
                .allowTransition(openCurlyBracket, caseOption)
                .allowTransition(caseOption, caseOption, closeCurlyBracket)
                .build();

        return new SwitchFSM(matrix, new ExceptionThrower<>(CompilingException::new));
    }

    private SwitchFSM(TransitionMatrix<SwitchContext, CompilingException> transitionMatrix,
                      ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
