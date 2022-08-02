package com.teamdev.runtime.value.type;

import com.google.common.base.Preconditions;

public class StringValue implements Value {

    private final String string;

    public StringValue(String value) {
        this.string = Preconditions.checkNotNull(value);
    }

    @Override
    public void acceptVisitor(ValueVisitor visitor) {
        visitor.visit(this);
    }

    public String string() {
        return string;
    }

    @Override
    public String toString() {
        return string;
    }
}
