package com.teamdev.meador.programelement;

/**
 * Abstract factory for {@link ProgramElementCompiler}.
 */
public interface ProgramElementCompilerFactory {

    ProgramElementCompiler create(ProgramElement statement);
}
