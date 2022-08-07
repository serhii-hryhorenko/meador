package com.teamdev.meador.programelement.forloop.fsmimpl;

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

    void setVariableDeclaration(Command variableDeclaration) {
        this.variableDeclaration = Preconditions.checkNotNull(variableDeclaration);
    }

    public Command repeatCondition() {
        return Preconditions.checkNotNull(repeatCondition);
    }

    void setRepeatCondition(Command repeatCondition) {
        this.repeatCondition = Preconditions.checkNotNull(repeatCondition);
    }

    public Command updateVariableStatement() {
        return updateVariableStatement;
    }

    void setUpdateVariableStatement(Command updateVariableStatement) {
        this.updateVariableStatement = Preconditions.checkNotNull(updateVariableStatement);
    }

    public Command loopBody() {
        return Preconditions.checkNotNull(loopBody);
    }

    void setLoopBody(Command loopBody) {
        this.loopBody = Preconditions.checkNotNull(loopBody);
    }
}
