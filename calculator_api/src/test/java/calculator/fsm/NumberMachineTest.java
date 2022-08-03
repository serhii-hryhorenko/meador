package calculator.fsm;

import calculator.AbstractResolvingTest;
import com.google.common.testing.NullPointerTester;
import com.teamdev.machine.number.NumberMachine;
import com.teamdev.fsm.ExceptionThrower;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import java.util.function.Supplier;
import java.util.stream.Stream;

class NumberMachineTest extends AbstractResolvingTest {

    static Stream<Arguments> positiveCases() {
        return Stream.of(
                Arguments.of("54", 54, "Positive integer interpretation error."),
                Arguments.of("1.02", 1.02, "Positive float interpretation error."),
                Arguments.of("-1", -1, "Negative integer interpretation error."),
                Arguments.of("-38.04", -38.04, "Negative float interpretation error."),
                Arguments.of("007", 7, "Number starting with zero interpretation error.")
        );
    }

    static Stream<Arguments> negativeCases() {
        return Stream.of(
                Arguments.of("0..0", 2, "Double dot in number is interpreted."),
                Arguments.of("0,7", 1, "Coma is interpreted as dot."),
                Arguments.of("0. 7", 2, "Whitespace wasn't ignored during evaluation.")

        );
    }

    @Test
    void testNull() {
        var nullPointerTester = new NullPointerTester();
        nullPointerTester.testAllPublicConstructors(NumberMachine.class);
        nullPointerTester.testAllPublicInstanceMethods(NumberMachine.create(new ExceptionThrower<>(
                () -> new IllegalArgumentException("Null value wasn't expected."))));
    }
}

