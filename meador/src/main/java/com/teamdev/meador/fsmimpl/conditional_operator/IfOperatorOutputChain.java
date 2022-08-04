package com.teamdev.meador.fsmimpl.conditional_operator;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.Command;

/**
 * Output chain for {@link IfOperatorMachine}.
 * Provides access to the parsing result of an {@code if} operator.
 */
public class IfOperatorOutputChain {
    private Command condition;
    private Command statements;

    public Command condition() {
        return Preconditions.checkNotNull(condition);
    }

    public void setCondition(Command condition) {
        this.condition = Preconditions.checkNotNull(condition);
    }

    public Command statements() {
        return Preconditions.checkNotNull(statements);
    }

    public void setStatements(Command statements) {
        this.statements = Preconditions.checkNotNull(statements);
    }
}
