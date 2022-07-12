package com.teamdev.meador.compiler;

import com.teamdev.fsm.InputSequence;
import com.teamdev.meador.runtime.Command;

import java.util.Optional;

/**
 * Abstract compiler for a Meador statement.
 */
@FunctionalInterface
public interface StatementCompiler {

    Optional<Command> compile(InputSequence inputSequence) throws CompilingException;
}
