package com.teamdev.meador;

import com.google.common.base.Preconditions;
import com.teamdev.meador.compiler.element.procedure.ValidatedProcedure;
import com.teamdev.meador.compiler.element.procedure.ValidatedProcedureFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link ValidatedProcedureFactory} implementation with basic Meador procedures.
 */
public class ValidatedProcedureFactoryImpl implements ValidatedProcedureFactory {
    private final Map<String, ValidatedProcedure> procedures = new HashMap<>();

    public ValidatedProcedureFactoryImpl() {
        procedures.put("print", new ValidatedProcedure.Builder()
                .setAction((values, environment) -> environment.output().println(values.toString()))
                .setMinArguments(1)
                .setMaxArguments(10)
                .build());

        procedures.put("clear", new ValidatedProcedure.Builder()
                .setAction((values, environment) -> environment.memory().clear())
                .build());

        procedures.put("flush", new ValidatedProcedure.Builder()
                .setAction((values, environment) -> environment.output().flush())
                .build());
    }

    @Override
    public ValidatedProcedure create(String name) {
        return procedures.get(Preconditions.checkNotNull(name));
    }

    @Override
    public boolean hasProcedure(String procedure) {
        return procedures.containsKey(Preconditions.checkNotNull(procedure));
    }
}
