package com.teamdev.calculator_api;

/**
 * Tiny type Value-Object for the result of calculation of {@link Calculator}.
 */

public final class Output {

    private final double result;

    public Output(double result) {
        this.result = result;
    }

    public double getResult() {
        return result;
    }
}
