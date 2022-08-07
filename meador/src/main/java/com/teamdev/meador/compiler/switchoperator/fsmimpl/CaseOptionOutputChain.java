package com.teamdev.meador.compiler.switchoperator.fsmimpl;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.Command;

/**
 * Output chain for {@link CaseOptionMachine}.
 */
public class CaseOptionOutputChain {
    private Command condition;
    private Command statement;

    public Command condition() {
        return Preconditions.checkNotNull(condition);
    }

    public void setCondition(Command condition) {
        this.condition = Preconditions.checkNotNull(condition);
    }

    public Command statement() {
        return Preconditions.checkNotNull(statement);
    }

    public void setStatement(Command statement) {
        this.statement = Preconditions.checkNotNull(statement);
    }
}
