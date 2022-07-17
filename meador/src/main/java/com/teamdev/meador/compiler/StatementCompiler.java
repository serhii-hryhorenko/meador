package com.teamdev.meador.compiler;

import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.runtime.Command;

import java.util.Optional;

/**
 * Abstract compiler for a Meador statement.
 */
@FunctionalInterface
public interface StatementCompiler {

    Optional<Command> compile(InputSequenceReader inputSequence) throws CompilingException;
}
