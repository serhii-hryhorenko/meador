package com.teamdev.meador.fsmimpl.conditional_operator;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.Command;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Output chain for {@link ConditionalOperatorMachine}.
 * Provides access to the parsing result of a sequence of conditional operators.
 */
public class ConditionalOperatorOutputChain {
    private final List<IfOperatorOutputChain> conditionalOperators = new LinkedList<>();
    private Command elseStatementList;

    public boolean elseInstructionPresent() {
        return Objects.nonNull(elseStatementList);
    }

    public void addConditionalOperator(IfOperatorOutputChain ifOperator) {
        conditionalOperators.add(Preconditions.checkNotNull(ifOperator));
    }

    public List<IfOperatorOutputChain> conditionalOperators() {
        return Collections.unmodifiableList(conditionalOperators);
    }

    public Command elseStatementList() {
        return Preconditions.checkNotNull(elseStatementList);
    }

    public void setElseStatementList(Command elseStatementList) {
        this.elseStatementList = Preconditions.checkNotNull(elseStatementList);
    }
}
