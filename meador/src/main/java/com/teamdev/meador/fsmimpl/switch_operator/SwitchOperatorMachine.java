package com.teamdev.meador.fsmimpl.switch_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchOperatorOutputChain;
import com.teamdev.meador.compiler.statement.switch_operator.CaseOptionOutputChain;
import com.teamdev.meador.fsmimpl.util.BracketedValueMachine;

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
                .setAcceptor((reader, outputSequence) ->
                        TextIdentifierMachine.acceptKeyword(reader, SWITCH, exceptionThrower))
                .build();

        var matchedValue = new State.Builder<SwitchOperatorOutputChain, CompilingException>()
                .setName("MATCHED VALUE")
                .setAcceptor(BracketedValueMachine.create(factory, READ_VARIABLE, SwitchOperatorOutputChain::setMappedValue))
                .build();

        var openCurlyBracket = new State.Builder<SwitchOperatorOutputChain, CompilingException>()
                .setName("OPEN CURLY BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('{'))
                .build();

        var caseOption = new State.Builder<SwitchOperatorOutputChain, CompilingException>()
                .setName("CASE OPTION")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var context = new CaseOptionOutputChain();

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
                .allowTransition(switchKeyword, matchedValue)
                .allowTransition(matchedValue, openCurlyBracket)
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
