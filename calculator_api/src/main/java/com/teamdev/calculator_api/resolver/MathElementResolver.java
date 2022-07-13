package com.teamdev.calculator_api.resolver;

import com.teamdev.calculator_api.MathElementResolverFactoryImpl;
import com.teamdev.fsm.InputSequence;
import com.teamdev.runtime.value.type.Value;

import java.util.Optional;

/**
 * Runs {@code FiniteStateMachine} with needed container for the result of its work and give it
 * back.
 * Requires only {@link InputSequence} to resolve input. See {@link MathElementResolverFactoryImpl}
 * for details of implementation.
 */
@FunctionalInterface
public interface MathElementResolver {

    Optional<Value> resolve(InputSequence input) throws ResolvingException;
}
