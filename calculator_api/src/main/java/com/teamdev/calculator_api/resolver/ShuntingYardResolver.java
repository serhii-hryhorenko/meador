package com.teamdev.calculator_api.resolver;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.runtime.value.ShuntingYard;
import com.teamdev.runtime.value.type.Value;

import java.util.Optional;

public final class ShuntingYardResolver implements MathElementResolver {

    private final StateAcceptor<ShuntingYard, ResolvingException> shuntingYardStateAcceptor;

    public ShuntingYardResolver(StateAcceptor<ShuntingYard, ResolvingException> acceptor) {
        this.shuntingYardStateAcceptor = Preconditions.checkNotNull(acceptor);
    }

    @Override
    public Optional<Value> resolve(InputSequenceReader input) throws ResolvingException {
        var yard = new ShuntingYard();

        if (shuntingYardStateAcceptor.accept(input, yard)) {
            return Optional.of(yard.popResult());
        }

        return Optional.empty();
    }
}
