package com.teamdev.calculator_api.resolver;

/**
 * Abstract factory for {@link MathElementResolver}.
 */
public interface MathElementResolverFactory {

    MathElementResolver create(MathElement machine);
}
