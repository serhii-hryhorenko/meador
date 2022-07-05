package com.teamdev.meador.runtime;

@FunctionalInterface
public interface Command {

    void execute(RuntimeEnvironment runtimeEnvironment);
}
