package com.teamdev.meador.compiler.statement.procedure;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;

public class ValidatedProcedureFactory {

    private final Map<String, ValidatedProcedure> procedures = new HashMap<>();

    public ValidatedProcedureFactory() {

        procedures.put("print", new ValidatedProcedure.Builder()
                .setAction((values, environment) -> environment.output()
                                                               .println(values.toString()))
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

    public ValidatedProcedure create(String name) {
        return procedures.get(Preconditions.checkNotNull(name));
    }

    public boolean hasProcedure(String procedure) {
        return procedures.containsKey(Preconditions.checkNotNull(procedure));
    }
}
