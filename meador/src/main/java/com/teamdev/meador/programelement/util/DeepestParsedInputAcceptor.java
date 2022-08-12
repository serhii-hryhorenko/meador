package com.teamdev.meador.programelement.util;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.meador.programelement.SyntaxException;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/**
 * "One of" {@link StateAcceptor} which executes run acceptor on detached output chain and executes
 * one which parsed the most symbols.
 *
 * @param <O>
 *         output chain
 */
public class DeepestParsedInputAcceptor<O> implements StateAcceptor<O, SyntaxException> {

    private final List<StateAcceptor<O, SyntaxException>> acceptors;
    private final Supplier<O> outputChainSupplier;

    @SafeVarargs
    public DeepestParsedInputAcceptor(Supplier<O> outputChainSupplier,
                                      StateAcceptor<O, SyntaxException>... acceptors) {
        this.acceptors = List.of(acceptors);
        this.outputChainSupplier = Preconditions.checkNotNull(outputChainSupplier);
    }

    @Override
    public boolean accept(InputSequenceReader reader, O outputChain) throws SyntaxException {
        var optionalCandidate = acceptors.stream()
                .max(Comparator.comparing(o -> o.parseInDepth(reader, outputChainSupplier)));

        return optionalCandidate.isPresent() && optionalCandidate.get()
                                                                 .accept(reader, outputChain);
    }
}
