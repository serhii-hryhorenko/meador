package com.teamdev.meador.fsmimpl.while_loop;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.Command;

public class WhileLoopOutputChain {
    private Command condition;
    private Command statementList;

    public Command condition() {
        return Preconditions.checkNotNull(condition);
    }

    public void setCondition(Command condition) {
        this.condition = Preconditions.checkNotNull(condition);
    }

    public Command loopBodyStatements() {
        return Preconditions.checkNotNull(statementList);
    }

    public void setLoopBodyStatements(Command statementList) {
        this.statementList = Preconditions.checkNotNull(statementList);
    }
}
