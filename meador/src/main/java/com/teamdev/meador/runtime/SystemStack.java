package com.teamdev.meador.runtime;

import com.google.common.base.Preconditions;
import com.teamdev.math.ShuntingYard;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A stack of {@link ShuntingYard} where {@link Command} is evaluated.
 */
public final class SystemStack {
    private final Deque<ShuntingYard> stack = new ArrayDeque<>();

    SystemStack() {
        create();
    }

    public void create() {
        stack.push(new ShuntingYard());
    }

    public ShuntingYard peek() {
        Preconditions.checkState(!stack.isEmpty());
        return stack.peek();
    }

    public ShuntingYard pop() {
        return stack.pop();
    }
}
