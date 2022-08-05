package meador;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static org.junit.jupiter.params.provider.Arguments.of;

class WhileLoopOperatorTest extends MeadorTest {

    static Stream<Arguments> positiveCases() {
        return Stream.of(
                of("i = 0; while (i < 5) { print(++i); }",
                   "[1.0]" + lineSeparator() +
                           "[2.0]" + lineSeparator() +
                           "[3.0]" + lineSeparator() +
                           "[4.0]" + lineSeparator() +
                           "[5.0]",
                   "While operator is broken."),

                of("while (false) { print(true); } print(false);",
                   "[false]",
                   "Loop body was executed with false condition."),
                of("""
                           i = 0;
                           j = 0;
                           sum = 0;
                           while (i < 5) {
                               while(j < 5) {
                                   sum = ++i + ++j;
                               }
                           }
                                                   
                           print(sum);
                           """,
                   "[10.0]",
                   "Nested while loop is broken.")
        );
    }

    static Stream<Arguments> negativeCases() {
        return Stream.of(
                of("while false) { flush(); }", "Omitted bracket is ignored."),
                of("while (false) flush();", "Omitted curly brackets are ignored."),
                of("while (true) flush();", "Infinite loop is ignored.")
        );
    }
}
