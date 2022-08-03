package calculator.fsm;

import calculator.AbstractResolvingTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class FunctionMachineTest extends AbstractResolvingTest {

    static Stream<Arguments> positiveCases() {

        return Stream.of(
                Arguments.of("pi()", Math.PI, "No argument functions are not recognized."),
                Arguments.of("sin(pi())", StrictMath.sin(Math.PI),
                             "Functions inside functions do not work."),
                Arguments.of("sum(10, 2 ^ 2, 3)", 17, "Many argument functions do not work")
        );
    }

    static Stream<Arguments> negativeCases() {

        return Stream.of(
                Arguments.of("pi(0)", 5, "No argument took argument."),
                Arguments.of("sin(1, 2)", 9, "More than maximum arguments was taken."),
                Arguments.of("sum(10)", 7, "Not enough arguments were given.")
        );
    }

}