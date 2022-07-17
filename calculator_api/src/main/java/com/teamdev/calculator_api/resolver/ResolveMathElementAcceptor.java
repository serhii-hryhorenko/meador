package com.teamdev.calculator_api.resolver;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.fsm.StateAcceptor;
import com.teamdev.runtime.value.type.Value;

import java.util.function.BiConsumer;

public class ResolveMathElementAcceptor<O> implements StateAcceptor<O, ResolvingException> {

    private final MathElementResolverFactory factory;

    private final MathElement element;

    private final BiConsumer<O, Value> resultConsumer;

    public ResolveMathElementAcceptor(
            MathElementResolverFactory factory,
            MathElement element,
            BiConsumer<O, Value> resultConsumer) {

        this.factory = Preconditions.checkNotNull(factory);
        this.element = Preconditions.checkNotNull(element);
        this.resultConsumer = Preconditions.checkNotNull(resultConsumer);
    }

    @Override
    public boolean accept(InputSequenceReader inputSequence, O outputSequence) throws ResolvingException {
        var resolver = factory.create(element);

        var optionalResult = resolver.resolve(inputSequence);
        optionalResult.ifPresent(value -> resultConsumer.accept(outputSequence, value));

        return optionalResult.isPresent();
    }
}
