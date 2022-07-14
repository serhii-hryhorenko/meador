package com.teamdev.meador.fsmimpl.switch_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.compiler.StatementType;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchContext;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchOptionContext;

/**
 * {@link FiniteStateMachine} implementation for recognizing {@code switch} operator in Meador programs.
 * <p>Grammar reference:
 * {@code switch (variable) {
 * <p>
 * case option1: {list_of_statements}
 * <p>
 * case option2: {list_of_statements}
 * <p>
 * …
 * <p>
 * case optionN: {list_of_statements}
 * <p>
 * default: {list_of_statements}
 * }
 * }
 * </p>
 */
public class SwitchFSM extends FiniteStateMachine<SwitchContext, CompilingException> {

    private SwitchFSM(TransitionMatrix<SwitchContext, CompilingException> transitionMatrix,
                      ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }

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
                .setAcceptor(new CompileStatementAcceptor<>(factory, StatementType.MEMORY_VALUE, SwitchContext::setValueToMatch))
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
                .setTemporary(true)
                .build();

        var defaultOption = new State.Builder<SwitchContext, CompilingException>()
                .setName("DEFAULT OPTION")
                .setAcceptor(DefaultOptionFSM.create(factory))
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
                .allowTransition(caseOption, caseOption, defaultOption)
                .allowTransition(defaultOption, closeCurlyBracket)
                .build();

        return new SwitchFSM(matrix, new ExceptionThrower<>(CompilingException::new));
    }
}
