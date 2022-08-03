package com.teamdev.meador.fsmimpl.switch_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchOperatorOutputChain;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchOptionContext;

import static com.teamdev.meador.compiler.ProgramElement.READ_VARIABLE;

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
public class SwitchOperatorMachine extends FiniteStateMachine<SwitchOperatorOutputChain, CompilingException> {

    private static final String SWITCH = "switch";

    public static SwitchOperatorMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var initial = State.<SwitchOperatorOutputChain, CompilingException>initialState();

        var exceptionThrower = new ExceptionThrower<>(CompilingException::new);

        var switchKeyword = new State.Builder<SwitchOperatorOutputChain, CompilingException>()
                .setName("SWITCH")
                .setAcceptor((inputSequence, outputSequence) ->
                        TextIdentifierMachine.acceptKeyword(inputSequence, SWITCH, exceptionThrower))
                .build();

        var openBracket = new State.Builder<SwitchOperatorOutputChain, CompilingException>()
                .setName("OPEN BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('('))
                .build();

        var expressionToMatch = new State.Builder<SwitchOperatorOutputChain, CompilingException>()
                .setName("EXPRESSION TO MATCH")
                .setAcceptor(new CompileStatementAcceptor<>(factory, READ_VARIABLE, SwitchOperatorOutputChain::setMappedValue))
                .build();

        var closeBracket = new State.Builder<SwitchOperatorOutputChain, CompilingException>()
                .setName("CLOSE BRACKET")
                .setAcceptor(StateAcceptor.acceptChar(')'))
                .build();

        var openCurlyBracket = new State.Builder<SwitchOperatorOutputChain, CompilingException>()
                .setName("OPEN CURLY BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('{'))
                .build();

        var caseOption = new State.Builder<SwitchOperatorOutputChain, CompilingException>()
                .setName("CASE OPTION")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var context = new SwitchOptionContext();

                    if (CaseOptionMachine.create(factory).accept(inputSequence, context)) {
                        outputSequence.addOption(context);
                        return true;
                    }

                    return false;
                })
                .setTemporary()
                .build();

        var defaultOption = new State.Builder<SwitchOperatorOutputChain, CompilingException>()
                .setName("DEFAULT OPTION")
                .setAcceptor(DefaultOptionMachine.create(factory))
                .build();

        var closeCurlyBracket = new State.Builder<SwitchOperatorOutputChain, CompilingException>()
                .setName("CLOSE CURLY BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('}'))
                .setFinal()
                .build();

        var matrix = new TransitionMatrixBuilder<SwitchOperatorOutputChain, CompilingException>()
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

        return new SwitchOperatorMachine(matrix, exceptionThrower);
    }

    private SwitchOperatorMachine(TransitionMatrix<SwitchOperatorOutputChain, CompilingException> transitionMatrix,
                                  ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
