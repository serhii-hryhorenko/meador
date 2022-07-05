package com.teamdev.meador;

import com.teamdev.meador.compiler.Compiler;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.runtime.RuntimeEnvironment;

public class Meador {

    public Output execute(Program program) throws CompilingException {

        var compiler = new Compiler();
        var environment = new RuntimeEnvironment();

        compiler.compile(program)
            .ifPresent(command -> command.execute(environment));

        return new Output(environment.console());
    }
}
