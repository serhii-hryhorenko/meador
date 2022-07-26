package meador;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;

public class UnaryOperatorTest extends MeadorTest {

    static Stream<Arguments> positiveCases() {
        return Stream.of(
                of("a = 4; b = ++a; print(b);", "[5.0]", "Prefix increment is broken."),
                of("a = 4; b = a++; print(a, b);", "[5.0, 4.0]", "Postfix increment is broken."),
                of("a = 4; b = ++a; print(!b);", "[120.0]", "Factorial operator is broken."),
                of("a = 4; b = ++a; print(!b, b);", "[120.0, 5.0]", "Operator mutated the value of the variable by mistake.")
        );
    }

    static Stream<Arguments> negativeCases() {
        return Stream.of(
                of("a = 5; print(++a++);", "Two unary operators with one operand were ignored."),
                of("a = 5; print(a!);", "Prefix operator usage in postfix position was ignored."),
                of("a = true; print(++a);", "Type mismatch was ignored.")
        );
    }
}
