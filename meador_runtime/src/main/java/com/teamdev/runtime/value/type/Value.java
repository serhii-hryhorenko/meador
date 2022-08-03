package com.teamdev.runtime.value.type;

import com.teamdev.runtime.MeadorRuntimeException;

public interface Value {

    void acceptVisitor(ValueVisitor visitor) throws MeadorRuntimeException;

    @Override
    String toString();
}
