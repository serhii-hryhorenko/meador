package meador;

import com.teamdev.meador.Meador;
import com.teamdev.meador.Program;
import com.teamdev.meador.compiler.CompilingException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MeadorCompileTest {
    private static final Logger logger = LoggerFactory.getLogger(MeadorCompileTest.class);

    private final Meador executor = new Meador();

    static Stream<Arguments> positiveCases() {

        return Stream.of(
                Arguments.of("a = 5; print(a);", "[5.0]"),
                Arguments.of("a = 5 > 2; print(a);", "[true]"),
                Arguments.of("a = 5 > 2 ^ 2 * 2; print(a);", "[false]"),
                Arguments.of("a = 5; b = a + 5; print(a+b, a);", "[15.0, 5.0]"),
                Arguments.of("abc = (2 + 2) * 2; bcd = (abc + 1) * 2; print(bcd);", "[18.0]"),
                Arguments.of("a = pi(); b = a ^ 2 * 10; print(b);", "[" +
                        StrictMath.pow(Math.PI, 2) * 10 + ']'),
                Arguments.of("pi = pi(); print(pi, pi());", "[" + Math.PI +", " + Math.PI + ']'),
                Arguments.of("pi = pi(); print(pi, pi()); pi = 2 * pi;", "[" + Math.PI +", " + Math.PI + ']'),

                Arguments.of("""
                        a = 5;
                        switch (a) {
                        case 5: { print(a); }
                        case 2: { a = 5; }
                        default: { print(450); }
                        }
                        """, "[5.0]"),
                Arguments.of("""
                        a = pi();
                        switch (a) {
                        case 0: { flush(); }
                        case pi(): { print(3, 0.14); }
                        default: { print(450); }
                        }
                        """, "[3.0, 0.14]"),
                Arguments.of("""
                        a = pi();
                        switch (a) {
                        case 0: { flush(); }
                        default: { print(450); }
                        }
                        """, "[450.0]")
        );
    }

    static Stream<Arguments> negativeCases() {
        return Stream.of(Arguments.of("var a; b = a + 5;"),
                         Arguments.of("println(42, b);"),
                         Arguments.of("a = 5 + b12;"),
                         Arguments.of("a = 5"));
    }

    @ParameterizedTest
    @MethodSource("positiveCases")
    void positiveCases(String code, String expected) throws CompilingException {
        var output = executor.execute(new Program(code));
        logger.info(output.toString());
        assertEquals(expected + System.lineSeparator(), output.toString());
    }

    @ParameterizedTest
    @MethodSource("negativeCases")
    void negativeCases(String code) {
        var program = new Program(code);

        assertThrows(CompilingException.class, () -> executor.execute(program));
    }
}