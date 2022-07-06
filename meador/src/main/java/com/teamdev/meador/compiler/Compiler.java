package com.teamdev.meador.compiler;

import com.teamdev.fsm.InputSequence;
import com.teamdev.meador.Program;
import com.teamdev.meador.compiler.fsmimpl.CompilerFSM;
import com.teamdev.meador.runtime.Command;

import java.util.ArrayList;
import java.util.Optional;

public class Compiler {

    public Optional<Command> compile(Program program) throws CompilingException {

        var inputSequence = new InputSequence(program.getCode());

        StatementCompilerFactory factory = new StatementCompilerFactoryImpl();

        var compiler = factory.create(StatementType.PROGRAM);

        return compiler.compile(inputSequence);
    }
}
