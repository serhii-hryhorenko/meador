package com.teamdev.meador.fsmimpl.conditional_operator;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.Command;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ConditionalOperatorOutputChain {

    private final List<IfOperatorOutputChain> ifOperators = new LinkedList<>();
    private Command elseStatements;

    public boolean elseInstructionPresent() {
        return Objects.nonNull(elseStatements);
    }

    public void addIfOperator(IfOperatorOutputChain ifOperator) {
        ifOperators.add(Preconditions.checkNotNull(ifOperator));
    }

    public List<IfOperatorOutputChain> ifOperators() {
        return ifOperators;
    }

    public Command elseStatements() {
        return Preconditions.checkNotNull(elseStatements);
    }

    public void setElseStatements(Command elseStatements) {
        this.elseStatements = Preconditions.checkNotNull(elseStatements);
    }
}
