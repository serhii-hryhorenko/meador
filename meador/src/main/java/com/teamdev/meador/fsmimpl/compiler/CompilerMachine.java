package com.teamdev.meador.fsmimpl.compiler;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.ProgramElementCompilerFactory;
import com.teamdev.runtime.Command;

import java.util.List;

import static com.teamdev.meador.compiler.ProgramElement.*;

/**
 * Provides {@link com.teamdev.meador.Meador} general language elements recognition.
 * Basic "one of" machine.
 */
public class CompilerMachine {

    public static StateAcceptor<List<Command>, CompilingException> create(ProgramElementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var compilingExceptionThrower = new ExceptionThrower<>(CompilingException::new);

        return FiniteStateMachine.oneOf("PROGRAM ELEMENT",

                new TransitionOneOfMatrixBuilder<List<Command>, CompilingException>()
                        .allowTransition(new CompileStatementAcceptor<List<Command>>(factory, CONDITIONAL_OPERATOR, List::add)
                                .named("CONDITIONAL OPERATOR"), true)
                        .allowTransition(new CompileStatementAcceptor<List<Command>>(factory, FOR_LOOP, List::add)
                                .named("FOR LOOP"), true)
                        .allowTransition(new CompileStatementAcceptor<List<Command>>(factory, SWITCH_OPERATOR, List::add)
                                .named("SWITCH OPERATOR"), true)
                        .allowTransition(new CompileStatementAcceptor<List<Command>>(factory, DATA_STRUCTURE_DECLARATION, List::add)
                                .named("DATA STRUCTURE DECLARATION"), true)
                        .allowTransition(new CompileStatementAcceptor<List<Command>>(factory, PROCEDURE, List::add)
                                .named("PROCEDURE"), true)
                        .allowTransition(new CompileStatementAcceptor<List<Command>>(factory, STRUCTURE_FIELD_ASSIGNMENT, List::add)
                                .named("STRUCTURE FIELD ASSIGNMENT"), true)
                        .allowTransition(new CompileStatementAcceptor<List<Command>>(factory, VARIABLE_ASSIGNMENT, List::add)
                                .named("VARIABLE ASSIGNMENT"), true),

                compilingExceptionThrower);
    }

    private CompilerMachine() {

    }
}
