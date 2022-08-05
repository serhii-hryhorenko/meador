package com.teamdev.meador;

import com.teamdev.meador.compiler.Compiler;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.RuntimeEnvironment;

/**
 * Entry point for Meador programmer. Takes user's {@link Program} and returns a system output as a
 * result of executing.
 */
public class Meador {

    public Output execute(Program program) throws InvalidProgramException {

        var compiler = new Compiler();
        var environment = new RuntimeEnvironment();

        try {
            var optionalProgram = compiler.compile(program);

            if (optionalProgram.isPresent()) {
                optionalProgram.get()
                               .execute(environment);
            }
        } catch (CompilingException ce) {
            throw new InvalidProgramException("ERROR CAUGHT DURING COMPILATION."
                                                      + System.lineSeparator() + ce.getMessage());
        } catch (MeadorRuntimeException mre) {
            throw new InvalidProgramException("RUNTIME ERROR WHILE EXECUTING PROGRAM."
                                                      + System.lineSeparator() + mre.getMessage());
        }

        return new Output(environment.console());
    }
}
