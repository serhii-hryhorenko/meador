package com.teamdev.runtime.operatorimpl.bioperator;

import com.google.common.base.Preconditions;
import com.teamdev.runtime.MeadorRuntimeException;
import com.teamdev.runtime.evaluation.TypeMismatchException;
import com.teamdev.runtime.evaluation.operandtype.BooleanValue;
import com.teamdev.runtime.evaluation.operandtype.BooleanValueVisitor;
import com.teamdev.runtime.evaluation.operandtype.Value;
import com.teamdev.runtime.evaluation.operator.AbstractBinaryOperator;

import java.util.function.BiPredicate;

public class BooleanValueBinaryOperator extends AbstractBinaryOperator {

    private final BiPredicate<Boolean, Boolean> operator;

    public BooleanValueBinaryOperator(BiPredicate<Boolean, Boolean> operator, Priority priority) {
        super(priority);
        this.operator = Preconditions.checkNotNull(operator);
    }

    @Override
    public Value apply(Value left, Value right) throws TypeMismatchException {
        var visitor = new BooleanValueVisitor();

        left.acceptVisitor(visitor);
        boolean one = visitor.value();

        right.acceptVisitor(visitor);
        boolean two = visitor.value();

        return new BooleanValue(operator.test(one, two));
    }
}
