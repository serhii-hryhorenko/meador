package com.teamdev.meador.fsmimpl.for_loop;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.Command;

/**
 * Output chain for {@link ForLoopOperatorMachine}.
 */
public class ForLoopOperatorOutputChain {
    private Command variableDeclaration;
    private Command repeatCondition;
    private Command updateVariableStatement;

    private Command loopBody;

    public Command variableDeclaration() {
        return Preconditions.checkNotNull(variableDeclaration);
    }

    public void setVariableDeclaration(Command variableDeclaration) {
        this.variableDeclaration = Preconditions.checkNotNull(variableDeclaration);
    }

    public Command repeatCondition() {
        return Preconditions.checkNotNull(repeatCondition);
    }

    public void setRepeatCondition(Command repeatCondition) {
        this.repeatCondition = Preconditions.checkNotNull(repeatCondition);
    }

    public Command updateVariableStatement() {
        return updateVariableStatement;
    }

    public void setUpdateVariableStatement(Command updateVariableStatement) {
        this.updateVariableStatement = Preconditions.checkNotNull(updateVariableStatement);
    }

    public Command loopBody() {
        return Preconditions.checkNotNull(loopBody);
    }

    public void setLoopBody(Command loopBody) {
        this.loopBody = Preconditions.checkNotNull(loopBody);
    }
}
