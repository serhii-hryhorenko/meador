package com.teamdev.meador.compiler.fsmimpl.switch_operator;

import com.teamdev.fsm.InputSequence;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactoryImpl;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwitchFSMTest {

    @Test
    public void parsingTest() throws CompilingException {
        InputSequence inputSequence = new InputSequence("""
                switch(50) {
                    case 10: { print(10); }
                    case 20: { print(2 * 10); }
                    case 50: { a = 5 ^ 2 * 2; print(a); }
                }
                """);

        var context = new SwitchContext();

        assertTrue(SwitchFSM.create(new StatementCompilerFactoryImpl()).accept(inputSequence, context));
    }
}