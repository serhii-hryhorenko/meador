package meador;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;

class IntegrationTest extends MeadorTest {

    static Stream<Arguments> positiveCases() {
        return Stream.of(
                of("a = 5; print(a);", "[5.0]",
                   "Numeric value wasn't printed."),

                of("a = true; print(a);", "[true]",
                   "Boolean literals are not recognized."),

                of("a = 2 >= 2; print(a);", "[true]",
                   "Boolean value wasn't printed."),

                of("a = 7 > 2 ^ 2 * 2; print(a);", "[false]",
                   "Relational expression evaluation is broken."),

                of("a = 5; b = a + 5; print(a+b, a);", "[15.0, 5.0]",
                   "Procedure parameters aren't recognized in a proper way."),

                of("abc = (2 + 2) * 2; bcd = (abc + 1) * 2; print(bcd);", "[18.0]",
                   "Variables are broken."),

                of("a = ( 2 ^ 3 + average(0, pi()) + 1 ) / 2; print(a);",
                   "[" + (((8 + Math.PI / 2) + 1) / 2) + ']',
                   "Nested brackets values are broken."),

                of("a = pi(); b = a ^ 2 * 10; print(b);",
                   "[" + StrictMath.pow(Math.PI, 2) * 10 + ']', "Function values are broken."),

                of("pi = pi(); print(pi, pi());", "[" + Math.PI + ", " + Math.PI + ']',
                   "Functions aren't recognized as procedure parameters."),

                of("pi = pi(); print(pi, pi()); pi = 2 * pi;", "[" + Math.PI + ", " + Math.PI + ']',
                   "Variable could not refer itself."),

                of("a = 5; switch(a) { case 5: { b = pi(); } default: { print(1); } } print(b);",
                   "[" + Math.PI + ']',
                   "Variable was not created inside switch operator."),

                of("a = true && false; b = a || 1 >= 0; c = (true && false) || false; print(a, b, c);",
                   "[false, true, false]",
                   "Boolean expressions are broken"),

                of("a = (5 >= (2 + 1) ^ 2 - 4) && false; print(a);", "[false]",
                   "Boolean expressions with parenthesis are broken."),

                of("a = 5; if (a >= 5 || false) { b = a ^ 2; } print(a + b);", "[30.0]",
                   "If statement is broken."),

                of("""
                           a = 5;
                           switch(a) {
                               case a: {
                                   if (a > 10) {
                                       print(true);
                                   } else {
                                       print(false);
                                   }
                               }
                               
                               default: {
                                   print(1);
                               }
                           }
                           """,
                   "[false]",
                   "If operator inside switch is broken."),

                of("""
                           age = 24;
                           Person { name, age };
                           jason = Person { `Jason`, age };
                                                   
                           for (i = 0; i < 25; i = ++i + 23;) {
                               if (jason.age == i) {
                                   print(jason.name + ` is ` + i + ` years old!`);
                               }
                           }
                           """,
                   "[Jason is 24.0 years old!]",
                   "Integration is failed.")
        );
    }

    static Stream<Arguments> negativeCases() {
        return Stream.of(
                of("var a; b = a + 5;", "Variable declaration with var was accepted."),
                of("println(42, b);", "Undeclared variable call was ignored."),
                of("a = 5 + b12;", "Bad operand syntax was ignored."),
                of("a = 5", "Semicolon absence was ignored."),
                of("a = true ^ false; b = a > 2;", "Type mismatch was ignored.")
        );
    }
}
