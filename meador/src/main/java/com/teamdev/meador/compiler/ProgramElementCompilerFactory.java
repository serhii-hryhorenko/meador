package com.teamdev.meador.compiler;

/**
 * Abstract factory for {@link ProgramElementCompiler}.
 */
public interface ProgramElementCompilerFactory {

    ProgramElementCompiler create(ProgramElement statement);
}
