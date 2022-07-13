package meador;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;

public class IntegrationTest extends MeadorTest {

    static Stream<Arguments> positiveCases() {
        return Stream.of(
                of("a = 5; print(a);", "[5.0]",
                        "Numeric value wasn't printed."),
                of("a = 5 > 2; print(a);", "[true]",
                        "Boolean value wasn't printed."),
                of("a = 5 > 2 ^ 2 * 2; print(a);", "[false]",
                        "Relational expression evaluation is broken."),
                of("a = 5; b = a + 5; print(a+b, a);", "[15.0, 5.0]",
                        "Procedure parameters aren't recognized in a proper way."),
                of("abc = (2 + 2) * 2; bcd = (abc + 1) * 2; print(bcd);", "[18.0]",
                        "Variables are broken."),
                of("print(pi() > 3);", "[true]", "No argument functions as relational operand are broken."),
                of("print(pi());", "[" + Math.PI + ']', "No argument functions as relational operand are broken."),
                of("print(sum(1, 2, 3));", "[6.0]", "Functions are broken."),
                of("a = pi(); b = a ^ 2 * 10; print(b);",
                        "[" + StrictMath.pow(Math.PI, 2) * 10 + ']', "Function values are broken."),
                of("pi = pi(); print(pi, pi());", "[" + Math.PI + ", " + Math.PI + ']',
                        "Functions aren't recognized as procedure parameters."),
                of("pi = pi(); print(pi, pi()); pi = 2 * pi;", "[" + Math.PI + ", " + Math.PI + ']',
                        "Variable could not refer itself."),
                of("a = 5; switch(a) { case 5: { b = pi(); } default: { print(1); } } print(b);", "[" + Math.PI + ']',
                        "Variable was not created inside switch operator."),
                of("a = average(1, 2, 3) > 1; print(a);", "[true]",
                        "Functions as left relational operand are not recognized."),
                of("print(1+2+3);", "[6.0]", "Three operands expression is broken.")
        );
    }

    static Stream<Arguments> negativeCases() {
        return Stream.of(of("var a; b = a + 5;", "Variable declaration with var was accepted."),
                of("println(42, b);", "Undeclared variable call was ignored."),
                of("a = 5 + b12;", "Bad operand syntax was ignored."),
                of("a = 5", "Semicolon absence was ignored."));
    }
}
