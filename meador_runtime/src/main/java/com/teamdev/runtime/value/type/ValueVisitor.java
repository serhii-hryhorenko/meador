package com.teamdev.runtime.value.type;

import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.value.type.bool.BooleanValue;
import com.teamdev.runtime.value.type.datastructure.DataStructureValue;
import com.teamdev.runtime.value.type.number.NumericValue;
import com.teamdev.runtime.value.type.string.StringValue;

public interface ValueVisitor {

    void visit(NumericValue value) throws MeadorRuntimeException;

    void visit(BooleanValue value) throws MeadorRuntimeException;

    void visit(DataStructureValue value) throws MeadorRuntimeException;

    void visit(StringValue value) throws MeadorRuntimeException;
}
