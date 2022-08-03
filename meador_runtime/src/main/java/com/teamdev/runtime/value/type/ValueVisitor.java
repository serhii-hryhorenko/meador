package com.teamdev.runtime.value.type;

import com.teamdev.runtime.value.type.bool.BooleanValue;
import com.teamdev.runtime.value.type.datastructure.DataStructureValue;
import com.teamdev.runtime.value.type.number.NumericValue;
import com.teamdev.runtime.value.type.string.StringValue;

public interface ValueVisitor {

    void visit(NumericValue value);

    void visit(BooleanValue value);

    void visit(DataStructureValue value);

    void visit(StringValue value);
}
