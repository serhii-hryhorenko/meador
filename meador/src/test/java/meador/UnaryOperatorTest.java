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
                of("a = 4; b = ++a; print(!b, b);", "[120.0, 5.0]",
                        "Operator mutated the value of the variable by mistake."),
                of("a = 5; print(a!, a);", "[5.0, 120.0]",
                        "Prefix operator usage in postfix position didn't changed variable value."),
                of("a = pi(); b = a~; print(~b, a);", "[3.0, 3.0]", "Integer cast operator is broken."),
                of("a = 5 != 1; print(not a);", "[false]", "Negation operator is broken."),
                of("a = 1; print(sum(a++, ±a, !a));", "[1.0]", "Operator returned values are wrong."),
                of("a = 1; print(++a, a);", "[2.0, 2.0]", "Prefix mutation is broken."),
                of("a = 1; b = ++a >= a++; print(not b, a);", "[false, 3.0]", "Unary operator is broken.")
        );
    }

    static Stream<Arguments> negativeCases() {
        return Stream.of(
                of("a = 5; print(++a!);", "Two unary operators with one operand were ignored."),
                of("a = 4; print(!++a);", "Two prefix unary operators with one operand were ignored."),
                of("a = 4; print(a~!);", "Two postfix unary operators with one operand were ignored."),
                of("a = true; print(++a);", "Boolean to numeric mismatch was ignored."),
                of("a = 5; print(not a);", "Numeric to boolean mismatch was ignored.")
        );
    }
}
