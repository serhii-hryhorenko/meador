package calculator.fsm;

import calculator.AbstractResolvingTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class BracketsFSMTest extends AbstractResolvingTest {

    static Stream<Arguments> positiveCases() {
        return Stream.of(
                Arguments.of("2 * (2 + 3)", 10, "Brackets are not interpreted"),
                Arguments.of("1 + (2 - 2)", 1, "Brackets aren't interpreted."),
                Arguments.of("2 * (7 + 3/(2 + 3))", 15.2,
                             "Double nesting brackets test has failed"),
                Arguments.of("(2)", 2, "Brackets with single value aren't interpreted."),
                Arguments.of("2 * (2 + 2)", 8, "Brackets do not prioritize expressions."),
                Arguments.of("2 * (7 + 3 / (2 + 3) ^ 4)", 14.0096,
                             "Double nesting brackets with degree test has failed"),
                Arguments.of("(sin(1) ^ 2 + cos(1) ^ 2) * 5", 5, "Brackets do not nest functions.")
        );
    }

    static Stream<Arguments> negativeCases() {
        return Stream.of(
                Arguments.of("1+3*(4+2", 8, "Opened but not closed brackets test has failed"),
                Arguments.of("1+3*4)+2", 5, "Closed but not opened brackets test has failed"),
                Arguments.of("()", 1, "Empty brackets test has failed")
        );
    }
}