package com.teamdev.runtime.function;

/**
 * Factory for {@link ValidatedProcedure}.
 */
public interface ValidatedProcedureFactory {

    ValidatedProcedure create(String name);

    boolean hasProcedure(String procedure);
}
