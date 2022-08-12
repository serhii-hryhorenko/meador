package com.teamdev.meador.programelement.switchoperator.fsmimpl;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.State;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionMatrix;
import com.teamdev.fsm.TransitionMatrixBuilder;
import com.teamdev.machine.util.TextIdentifierMachine;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.meador.programelement.util.BracketedValueMachine;

import static com.teamdev.meador.programelement.ProgramElement.READ_VARIABLE;

/**
 * {@link FiniteStateMachine} implementation for recognizing {@code switch} operator in Meador
 * programs.
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
public class SwitchOperatorMachine extends FiniteStateMachine<SwitchOperatorOutputChain, SyntaxException> {

    private static final String SWITCH = "switch";

    public static SwitchOperatorMachine create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var initial = State.<SwitchOperatorOutputChain, SyntaxException>initialState();

        var exceptionThrower = new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize switch operator."));

        var switchKeyword = new State.Builder<SwitchOperatorOutputChain, SyntaxException>()
                .setName("SWITCH")
                .setAcceptor((reader, outputSequence) ->
                                     TextIdentifierMachine.acceptKeyword(reader, SWITCH,
                                                                         exceptionThrower))
                .build();

        var matchedValue = new State.Builder<SwitchOperatorOutputChain, SyntaxException>()
                .setName("MATCHED VALUE")
                .setAcceptor(BracketedValueMachine.create(factory, READ_VARIABLE,
                                                          SwitchOperatorOutputChain::setMappedValue))
                .build();

        var openCurlyBracket = new State.Builder<SwitchOperatorOutputChain, SyntaxException>()
                .setName("OPEN CURLY BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('{'))
                .build();

        var caseOption = new State.Builder<SwitchOperatorOutputChain, SyntaxException>()
                .setName("CASE OPTION")
                .setAcceptor((inputSequence, outputSequence) -> {
                    var context = new CaseOptionOutputChain();

                    if (CaseOptionMachine.create(factory)
                            .accept(inputSequence, context)) {
                        outputSequence.addOption(context);
                        return true;
                    }

                    return false;
                })
                .setTemporary()
                .build();

        var defaultOption = new State.Builder<SwitchOperatorOutputChain, SyntaxException>()
                .setName("DEFAULT OPTION")
                .setAcceptor(DefaultOptionMachine.create(factory))
                .build();

        var closeCurlyBracket = new State.Builder<SwitchOperatorOutputChain, SyntaxException>()
                .setName("CLOSE CURLY BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('}'))
                .setFinal()
                .build();

        var matrix = new TransitionMatrixBuilder<SwitchOperatorOutputChain, SyntaxException>()
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

    private SwitchOperatorMachine(
            TransitionMatrix<SwitchOperatorOutputChain, SyntaxException> transitionMatrix,
            ExceptionThrower<SyntaxException> thrower) {
        super(transitionMatrix, thrower);
    }
}
