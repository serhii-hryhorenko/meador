package com.teamdev.meador.compiler.element.procedure;

/**
 * Factory for {@link ValidatedProcedure}.
 */
public interface ValidatedProcedureFactory {

    ValidatedProcedure create(String name);

    boolean hasProcedure(String procedure);
}
