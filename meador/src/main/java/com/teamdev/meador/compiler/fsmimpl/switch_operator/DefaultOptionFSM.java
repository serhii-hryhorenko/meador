package com.teamdev.meador.compiler.fsmimpl.switch_operator;

import com.google.common.base.Preconditions;
import com.teamdev.fsm.*;
import com.teamdev.machine.util.TextIdentifierFSM;
import com.teamdev.meador.compiler.CompileStatementAcceptor;
import com.teamdev.meador.compiler.CompilingException;
import com.teamdev.meador.compiler.StatementCompilerFactory;
import com.teamdev.meador.compiler.StatementType;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchContext;
import com.teamdev.meador.compiler.statement.switch_operator.SwitchOptionContext;
import com.teamdev.meador.runtime.Command;

import java.util.Optional;
import java.util.function.BiConsumer;

public class DefaultOptionFSM extends FiniteStateMachine<SwitchContext, CompilingException> {

    public static DefaultOptionFSM create(StatementCompilerFactory factory) {
        Preconditions.checkNotNull(factory);

        var defaultKeyword = new State.Builder<SwitchContext, CompilingException>()
                .setName("DEFAULT OPTION")
                .setAcceptor(new StateAcceptor<SwitchContext, CompilingException>() {
                    @Override
                    public boolean accept(InputSequence inputSequence, SwitchContext outputSequence) throws CompilingException {
                        var optionalWord = TextIdentifierFSM.execute(inputSequence, new ExceptionThrower<>(CompilingException::new));
                        return optionalWord.isPresent() && optionalWord.get().equals("default");
                    }
                })
                .build();

        var colon = new State.Builder<SwitchContext, CompilingException>()
                .setName("COLON")
                .setAcceptor(StateAcceptor.acceptChar(':'))
                .build();

        var openCurlyBracket = new State.Builder<SwitchContext, CompilingException>()
                .setName("OPEN CURLY BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('{'))
                .build();

        var executableExpression = new State.Builder<SwitchContext, CompilingException>()
                .setName("EXECUTABLE EXPRESSION")
                .setAcceptor(new CompileStatementAcceptor<>(factory, StatementType.PROGRAM, SwitchContext::setDefaultCommand))
                .build();

        var closeCurlyBracket = new State.Builder<SwitchContext, CompilingException>()
                .setName("CLOSE CURLY BRACKET")
                .setAcceptor(StateAcceptor.acceptChar('}'))
                .setFinite(true)
                .build();

        var matrix =
                TransitionMatrix.chainedTransitions(defaultKeyword, colon, openCurlyBracket, executableExpression, closeCurlyBracket);

        return new DefaultOptionFSM(matrix, new ExceptionThrower<>(CompilingException::new));
    }
    private DefaultOptionFSM(TransitionMatrix<SwitchContext, CompilingException> transitionMatrix, ExceptionThrower<CompilingException> thrower) {
        super(transitionMatrix, thrower);
    }
}
