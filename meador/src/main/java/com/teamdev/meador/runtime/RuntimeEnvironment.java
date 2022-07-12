package com.teamdev.meador.runtime;

import com.google.common.base.Preconditions;
import com.teamdev.meador.compiler.fsmimpl.datastructure.DataStructureTemplate;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Environment where a program is executed.
 * Provides an access to {@link Memory}, {@link SystemStack}, and IO.
 */
public class RuntimeEnvironment {

    private final Memory memory = new Memory();

    private final SystemStack stack = new SystemStack();

    private final Set<DataStructureTemplate> dataStructures = new HashSet<>();

    private final ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();

    private final PrintStream outputStream = new PrintStream(byteArrayOut);

    public Memory memory() {
        return memory;
    }

    public SystemStack stack() {
        return stack;
    }

    public PrintStream output() {
        return outputStream;
    }

    public OutputStream console() {
        return byteArrayOut;
    }

    public void addStructureTemplate(DataStructureTemplate dataStructure) {
        dataStructures.add(Preconditions.checkNotNull(dataStructure));
    }

    public Optional<DataStructureTemplate> getStructureTemplate(String structureName) {
        Preconditions.checkNotNull(structureName);
        return dataStructures
                .stream()
                .filter(dataStructureHolder -> dataStructureHolder.name().equals(structureName))
                .findFirst();
    }
}
