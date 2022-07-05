package com.teamdev.machine.util;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequence;
import com.teamdev.fsm.StateAcceptor;

import java.util.List;
import java.util.function.Predicate;

/**
 * Util state acceptor implementation for reading from {@link InputSequence} and writing to buffer.
 */
public class SymbolAcceptor<E extends Exception> implements StateAcceptor<StringBuilder, E> {

    private final Predicate<Character> condition;

    public SymbolAcceptor(Character... chars) {
        Preconditions.checkNotNull(chars);

        this.condition = character -> List.of(chars)
                                          .contains(character);
    }

    public SymbolAcceptor(Predicate<Character> condition) {

        this.condition = Preconditions.checkNotNull(condition);
    }

    @Override
    public boolean accept(InputSequence inputChain, StringBuilder outputChain) {
        Preconditions.checkNotNull(inputChain, outputChain);

        if (inputChain.canRead() && condition.test(inputChain.read())) {
            outputChain.append(inputChain.read());
            inputChain.next();
            return true;
        }

        return false;
    }
}
