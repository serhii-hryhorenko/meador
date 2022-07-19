package com.teamdev.meador.fsmimpl.conditional_operator;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.Command;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ConditionalOperatorContext {

    private final List<IfOperatorContext> ifOperators = new LinkedList<>();
    private Command elseStatements;

    public boolean elseInstructionPresent() {
        return Objects.nonNull(elseStatements);
    }

    public void addIfOperator(IfOperatorContext ifOperator) {
        ifOperators.add(Preconditions.checkNotNull(ifOperator));
    }

    public List<IfOperatorContext> ifOperators() {
        return ifOperators;
    }

    public Command elseStatements() {
        return Preconditions.checkNotNull(elseStatements);
    }

    public void setElseStatements(Command elseStatements) {
        this.elseStatements = Preconditions.checkNotNull(elseStatements);
    }
}
