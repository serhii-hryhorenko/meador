package com.teamdev.runtime;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.value.ShuntingYard;

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
        Preconditions.checkState(!stack.isEmpty());
        return stack.pop();
    }
}
