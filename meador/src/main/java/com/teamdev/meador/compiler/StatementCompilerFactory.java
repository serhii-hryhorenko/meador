package com.teamdev.meador.compiler;

public interface StatementCompilerFactory {

    StatementCompiler create(StatementType statement);
}
