package calculator;

import com.google.common.testing.NullPointerTester;
import com.teamdev.calculator_api.Calculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class CalculatorTest extends AbstractResolvingTest {

    static Stream<Arguments> positiveCases() {

        return Stream.of(
                Arguments.of("2", 2, "Evaluation of a single number is broken."),
                Arguments.of("-3.2    ", -3.2, "Single input value interpretation is broken."),
                Arguments.of("2 * 4", 8, "Multiplication failed."),
                Arguments.of("0 - 3.22", -3.22, "Subtraction does not work."),
                Arguments.of("1+2", 3, "Simple sum action test has failed."),
                Arguments.of("3 * 4 + 6 * 7 + 9 ^ 2", 135,
                             "Negative several digits test has failed."),
                Arguments.of("1+2*3", 7, "Several digits test has failed."),
                Arguments.of("sum(1, 2, 3)", 6, "Functions are not recognized."),
                Arguments.of("average(1, 2, 3) + sum(1, 2, 3)", 8, "Functions are not operands."),
                Arguments.of("2*4", 8, "Simple multiplication action test has failed."),
                Arguments.of("pi()", Math.PI, "No arguments functions are broken."),
                Arguments.of("sum(1, pi())", 1 + Math.PI, "Nested functions aren't interpreted.")
        );
    }

    static Stream<Arguments> negativeCases() {

        return Stream.of(
                Arguments.of("3.14.16", 4, "Double dot in float value is not detected."),
                Arguments.of("--100", 1, "Double sign before number is not detected."),
                Arguments.of("(2 + 2", 6, "Unmatched brackets."),
                Arguments.of("pi(1)", 5, "No arguments functions take argument."),
                Arguments.of("sin(1, 2, 3)", 12, "Limited arguments functions take arguments.")
        );
    }

    @Test
    void testNull() {
        var nullPointerTester = new NullPointerTester();
        nullPointerTester.testAllPublicConstructors(Calculator.class);
        nullPointerTester.testAllPublicInstanceMethods(getCalculator());
    }
}