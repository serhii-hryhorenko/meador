package com.teamdev.runtime;

/**
 * Runtime command which is created in compile time and evaluated on a runtime.
 */
@FunctionalInterface
public interface Command {

    void execute(RuntimeEnvironment runtimeEnvironment);
}
