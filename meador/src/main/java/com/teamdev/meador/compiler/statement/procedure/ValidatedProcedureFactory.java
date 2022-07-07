package com.teamdev.meador.compiler.statement.procedure;

/**
 * Factory for {@link ValidatedProcedure}.
 */
public interface ValidatedProcedureFactory {

    ValidatedProcedure create(String name);

    boolean hasProcedure(String procedure);
}
