package com.teamdev.runtime.value.operator.unaryoperator;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.type.Value;

import java.util.function.UnaryOperator;

public abstract class AbstractUnaryOperator implements UnaryOperator<Value> {

    private final Position position;
    private final boolean mutates;

    AbstractUnaryOperator(Position position) {
        this.position = Preconditions.checkNotNull(position);
        this.mutates = false;
    }

    AbstractUnaryOperator(Position position, boolean mutates) {
        this.position = Preconditions.checkNotNull(position);
        this.mutates = mutates;
    }

    public Position position() {
        return position;
    }

    public boolean mutates() {
        return mutates;
    }
}
