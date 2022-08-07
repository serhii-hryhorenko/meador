package com.teamdev.meador.programelement;

import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.runtime.Command;

import java.util.Optional;

/**
 * Abstract compiler for a Meador program element.
 */
@FunctionalInterface
public interface ProgramElementCompiler {

    Optional<Command> compile(InputSequenceReader reader) throws CompilingException;
}
