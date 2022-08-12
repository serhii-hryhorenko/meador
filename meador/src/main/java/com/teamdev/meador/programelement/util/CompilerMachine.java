package com.teamdev.meador.programelement.util;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.ExceptionThrower;
import com.teamdev.fsm.FiniteStateMachine;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.fsm.TransitionOneOfMatrixBuilder;
import com.teamdev.meador.programelement.SyntaxException;
import com.teamdev.meador.programelement.ProgramElementCompilerFactory;
import com.teamdev.runtime.Command;

import java.util.List;

import static com.teamdev.meador.programelement.ProgramElement.CONDITIONAL_OPERATOR;
import static com.teamdev.meador.programelement.ProgramElement.DATA_STRUCTURE_DECLARATION;
import static com.teamdev.meador.programelement.ProgramElement.FOR_LOOP;
import static com.teamdev.meador.programelement.ProgramElement.PROCEDURE;
import static com.teamdev.meador.programelement.ProgramElement.STRUCTURE_FIELD_ASSIGNMENT;
import static com.teamdev.meador.programelement.ProgramElement.SWITCH_OPERATOR;
import static com.teamdev.meador.programelement.ProgramElement.VARIABLE_ASSIGNMENT;
import static com.teamdev.meador.programelement.ProgramElement.WHILE_LOOP;

/**
 * Provides {@link com.teamdev.meador.Meador} general language elements recognition.
 * Basic "one of" machine.
 */
public class CompilerMachine {

    private CompilerMachine() {

    }

    public static StateAcceptor<List<Command>, SyntaxException> create(
            ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var compilingExceptionThrower = new ExceptionThrower<>(() -> new SyntaxException("Failed to recognize program element."));

        return FiniteStateMachine.oneOf(
                "PROGRAM ELEMENT",

                new TransitionOneOfMatrixBuilder<List<Command>, SyntaxException>()
                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(
                                        factory, CONDITIONAL_OPERATOR,
                                        List::add)
                                        .named("CONDITIONAL OPERATOR"), true)

                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(
                                        factory, WHILE_LOOP, List::add)
                                        .named("WHILE LOOP"), true)

                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(
                                        factory, FOR_LOOP, List::add)
                                        .named("FOR LOOP"), true)

                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(
                                        factory, SWITCH_OPERATOR, List::add)
                                        .named("SWITCH OPERATOR"), true)

                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(
                                        factory, DATA_STRUCTURE_DECLARATION,
                                        List::add)
                                        .named("DATA STRUCTURE DECLARATION"), true)

                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(
                                        factory, PROCEDURE, List::add)
                                        .named("PROCEDURE"), true)

                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(
                                        factory, STRUCTURE_FIELD_ASSIGNMENT,
                                        List::add)
                                        .named("STRUCTURE FIELD ASSIGNMENT"), true)
                        .allowTransition(
                                new CompileStatementAcceptor<List<Command>>(
                                        factory, VARIABLE_ASSIGNMENT,
                                        List::add)
                                        .named("VARIABLE ASSIGNMENT"), true),

                compilingExceptionThrower);
    }
}
