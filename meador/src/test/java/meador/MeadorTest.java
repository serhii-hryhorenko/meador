package meador;

import com.teamdev.meador.InvalidProgramException;
import com.teamdev.meador.Meador;
import com.teamdev.meador.Program;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class MeadorTest {
    private static final Logger logger = LoggerFactory.getLogger(MeadorTest.class);

    private final Meador executor = new Meador();

    static Stream<Arguments> positiveCases() {
        return Stream.empty();
    }

    static Stream<Arguments> negativeCases() {
        return Stream.empty();
    }

    @ParameterizedTest
    @MethodSource("positiveCases")
    void positiveCases(String code, String expected, String errorMessage) throws InvalidProgramException {
        var output = executor.execute(new Program(code));
        logger.info(output.toString());
        assertEquals(expected + System.lineSeparator(), output.toString(), errorMessage);
    }

    @ParameterizedTest
    @MethodSource("negativeCases")
    void negativeCases(String code, String errorMessage) {
        var program = new Program(code);
        assertThrows(InvalidProgramException.class, () -> executor.execute(program), errorMessage);
    }
}