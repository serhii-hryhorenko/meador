package com.teamdev.meador.fsmimpl.util;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.meador.compiler.CompilingException;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public class DeepestParsedInputAcceptor<O> implements StateAcceptor<O, CompilingException> {

    private final List<StateAcceptor<O, CompilingException>> acceptors;
    private final Supplier<O> outputChainSupplier;

    @SafeVarargs
    public DeepestParsedInputAcceptor(Supplier<O> outputChainSupplier,
                                      StateAcceptor<O, CompilingException>... acceptors) {
        this.acceptors = List.of(acceptors);
        this.outputChainSupplier = Preconditions.checkNotNull(outputChainSupplier);
    }

    @Override
    public boolean accept(InputSequenceReader reader, O outputChain) throws CompilingException {
        var optionalCandidate = acceptors.stream()
                .max(Comparator.comparing(o -> o.parseInDepth(reader, outputChainSupplier)));

        return optionalCandidate.isPresent() && optionalCandidate.get().accept(reader, outputChain);
    }
}
