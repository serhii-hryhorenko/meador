package com.teamdev.meador.compiler.statement.switch_operator;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.Command;

public class SwitchOptionContext {

    private Command condition;
    private Command statement;

    public Command condition() {
        return condition;
    }

    public void setCondition(Command condition) {
        this.condition = Preconditions.checkNotNull(condition);
    }

    public Command statement() {
        return statement;
    }

    public void setStatement(Command statement) {
        this.statement = Preconditions.checkNotNull(statement);
    }
}
