package calculator.fsm;

import calculator.AbstractResolvingTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class BinaryOperatorAcceptorTest extends AbstractResolvingTest {

    static Stream<Arguments> positiveCases() {
        return Stream.of(
                Arguments.of("4+5", 9, "Addition does not work."),
                Arguments.of("2 + 2 * 2", 6, "Priority for multiplication is broken."),
                Arguments.of("2 * 3 ^ 3", 54, "Priority for power is broken."),
                Arguments.of("1 + 2 * 3 ^ 4", 163, "Priority is broken."),
                Arguments.of("100 - -10", 110, "Subtraction of a negative does not work.")
        );
    }

    static Stream<Arguments> negativeCases() {
        return Stream.of(
                Arguments.of("10 + ", 5, "Unmatched second operand")
        );
    }
}