package meador;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static org.junit.jupiter.params.provider.Arguments.of;

public class ForLoopOperatorTest extends MeadorTest {

    static Stream<Arguments> positiveCases() {
        return Stream.of(
                of("""
                                for (i = 0; i < 4; i = i + 1;) {
                                     print(i);
                                }
                                """,
                        """
                                [0.0]
                                [1.0]
                                [2.0]
                                [3.0]""",
                        "Default use case for the for loop is broken."),

                of("""
                                for (i = 4; i > 0; i = i - 1;) {
                                     print(i);
                                }
                                """,
                        """
                                [4.0]
                                [3.0]
                                [2.0]
                                [1.0]""",
                        "Reverted default use case for the for loop is broken."),

                of("""
                                a = 0;
                                                                
                                for (i = 4; i > 0; i = i - 1;) {
                                     print(i);
                                     a = a + 1;
                                }
                                                                
                                print(a);
                                """,
                        """
                                [4.0]
                                [3.0]
                                [2.0]
                                [1.0]
                                [4.0]""",
                        "Loop body is not executed properly."),

                of("""
                                a = 0;
                                                                
                                for (i = 0; i < 5; i = i + 1;) {
                                    for (j = 0; j < 5; j = j + 1;) {
                                        a = a + 1;
                                    }
                                }
                                                                
                                print(a);
                                """,
                        "[25.0]",
                        "Nested for loop operators are broken."),

                of("""
                                for (i = 4; i > 5; i = i - 1;) {
                                     print(i);
                                }
                                                        
                                print(i);
                                """,
                        "[4.0]",
                        "Update statement executed after blocking condition."),

                of("""
                                for (i = 0; i < 2; i = i + 1;) {
                                     a = i > 0;
                                     print(a);
                                }
                                """,
                        "[false]" + lineSeparator() + "[true]",
                        "Initialized variable is broken.")
        );
    }

    static Stream<Arguments> negativeCases() {

        return Stream.of(
                of("""
                        for i = 0; i < 4; i = i + 1;){
                             print(i);
                        }
                        """, "Omitted open bracket was ignored."),

                of("""
                        for (i = 0 i < 4; i = i + 1;){
                             print(i);
                        }
                        """, "Omitted semicolon after variable declaration was ignored."),

                of("""
                        for (i = 0; i < 4 i = i + 1;){
                             print(i);
                        }
                        """, "Omitted semicolon after repeat condition was ignored."),

                of("""
                        for (i = 0; i < 4; i = i + 1) {
                             print(i);
                        }
                        """, "Omitted semicolon after variable declaration was ignored."),

                of("""
                        for (i = 0; i < 4; i = i + 1; {
                             print(i);
                        }
                        """, "Omitted bracket after for parameters declaration was ignored."),

                of("""
                        for (i = 0; i < 4; i = i + 1;)
                             print(i);
                        }
                        """, "Omitted curly bracket of loop body was ignored.")
        );
    }
}