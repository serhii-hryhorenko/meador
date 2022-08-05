package com.teamdev.calculator_api.resolver;

import com.teamdev.calculator_api.MathElementResolverFactoryImpl;
import com.teamdev.fsm.InputSequenceReader;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.evaluation.operandtype.Value;

import java.util.Optional;

/**
 * Runs {@code FiniteStateMachine} with needed container for the result of its work and give it
 * back.
 * Requires only {@link InputSequenceReader} to resolve input. See {@link
 * MathElementResolverFactoryImpl}
 * for details of implementation.
 */
@FunctionalInterface
public interface MathElementResolver {

    Optional<Value> resolve(InputSequenceReader input) throws ResolvingException,
                                                              MeadorRuntimeException;
}
