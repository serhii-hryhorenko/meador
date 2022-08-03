package com.teamdev.runtime.value.type.string;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.type.Value;
import com.teamdev.runtime.value.type.ValueVisitor;

public class StringValue implements Value {

    private final String string;

    public StringValue(String value) {
        this.string = Preconditions.checkNotNull(value);
    }

    @Override
    public void acceptVisitor(ValueVisitor visitor) {
        Preconditions.checkNotNull(visitor).visit(this);
    }

    public String string() {
        return string;
    }

    @Override
    public String toString() {
        return string;
    }
}
