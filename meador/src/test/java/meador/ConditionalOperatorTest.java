package meador;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;

public class ConditionalOperatorTest extends MeadorTest {

    static Stream<Arguments> positiveCases() {
        return Stream.of(
                of("""
                                if (0 < 2) {
                                    print(450);
                                }
                                """,
                        "[450.0]",
                        "If statement is broken."),
                of("""
                                if (0 > 2) {
                                    print(true);
                                } else {
                                    print(false);
                                }
                                """,
                        "[false]",
                        "Else operator is broken."),
                of("""
                                if (0 > 2) {
                                    print(1);
                                } else if (0 == 2) {
                                    print(2);
                                } else {
                                    print(3);
                                }
                                """,
                        "[3.0]",
                        "Else operator after if sequence is broken."),
                of("""
                                if (0 > 2) {
                                    print(1);
                                } else if (0 < 2) {
                                    print(2);
                                } else {
                                    print(3);
                                }
                                """,
                        "[2.0]",
                        "Else if operator is broken."),
                of("""
                                if (0 < 2) {
                                    print(1);
                                } else if (0 < 2) {
                                    print(2);
                                } else {
                                    print(3);
                                }
                                """,
                        "[1.0]",
                        "If operator in sequence is broken."),
                of("""
                                if (false) {
                                    print(5);
                                } if (true) {
                                    print(true);
                                }
                                """,
                        "[true]",
                        "Sequence of pure if operators is broken.")
        );
    }

    static Stream<Arguments> negativeCases() {
        return Stream.of(
                of("""
                                if (5) {
                                    print(5);
                                }
                                """,
                        "Numeric value in condition was ignored."),
                of("""
                                if (false) {
                                    print(5);
                                } else (true) {
                                    print(true);
                                }
                                """,
                        "Else with condition was ignored."),
                of("""
                                else {
                                    print(5);
                                }
                                """,
                        "Omitted if keyword was ignored."),
                of("""
                                if (false) {
                                    print(5);
                                } else {
                                    print(true);
                                } else {
                                    print(false);
                                }
                                """,
                        "Double else was ignored.")
        );
    }
}
