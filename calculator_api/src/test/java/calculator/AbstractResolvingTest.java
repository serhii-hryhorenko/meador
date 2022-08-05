package calculator;

import com.teamdev.calculator_api.Calculator;
import com.teamdev.calculator_api.InvalidExpressionException;
import com.teamdev.calculator_api.MathExpression;
import com.teamdev.calculator_api.Output;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractResolvingTest {

    private static final String POSITIVE_CASES = "positiveCases";
    private static final String NEGATIVE_CASES = "negativeCases";
    private final Calculator calculator = new Calculator();

    static Stream<Arguments> positiveCases() {
        return Stream.empty();
    }

    static Stream<Arguments> negativeCases() {
        return Stream.empty();
    }

    @ParameterizedTest
    @MethodSource(POSITIVE_CASES)
    void testPositiveCases(String mathExpression, double expected, String errorMessage) throws
                                                                                        InvalidExpressionException {
        Output result = null;
        try {
            result = calculator.calculate(new MathExpression(mathExpression));
        } catch (com.teamdev.runtime.MeadorRuntimeException ignored) {
        }

        assertEquals(expected, result.getResult(), errorMessage);
    }

    @ParameterizedTest
    @MethodSource(NEGATIVE_CASES)
    void testNegativeCases(String mathExpression, double index, String errorMessage) {
        var exception = assertThrows(InvalidExpressionException.class,
                                     () -> calculator.calculate(
                                             new MathExpression(mathExpression)));
        assertEquals(index, exception.getIndex(), errorMessage);
    }

    Calculator getCalculator() {
        return calculator;
    }
}
