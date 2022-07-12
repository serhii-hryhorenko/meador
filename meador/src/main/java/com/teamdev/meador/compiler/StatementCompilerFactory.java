package com.teamdev.meador.compiler;

/**
 * Abstract factory for {@link StatementCompiler}.
 */
public interface StatementCompilerFactory {

    StatementCompiler create(StatementType statement);
}
