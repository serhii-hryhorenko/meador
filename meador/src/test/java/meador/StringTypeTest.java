package meador;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;

class StringTypeTest extends MeadorTest {

    static Stream<Arguments> positiveCases() {
        return Stream.of(
                of("a = `Hello, World!`; print(a);", "[Hello, World!]",
                   "String literal is broken."),
                of("a = `Adam`; a = a + ` Johns`; print(a);", "[Adam Johns]",
                   "String expression is broken."),
                of("""
                           name = `Adam`;
                           surname = `Johns`;
                                                   
                           fullname = name + ` ` + surname;
                                                   
                           print(fullname);
                           """,
                   "[Adam Johns]",
                   "Long string expressions are broken."),
                of("""
                           str = `Year:`;
                           print(str + ` ` + 1994);
                           """,
                   "[Year: 1994.0]",
                   "Numeric to String conversion is failed."),

                of("a = 1994 + ` problems is ` + true; print(a);",
                   "[1994.0 problems is true]",
                   "Numeric to String conversion is failed."),

                of("print(`2` + true + false);",
                   "[2truefalse]",
                   "Boolean to String conversion is failed."),

                of("print(true + `false`);",
                   "[truefalse]",
                   "Boolean to String conversion is failed."),

                of("print(max(1, 2) + `abc` + 2, `abc`);",
                   "[2.0abc2.0, abc]",
                   "String expression is broken as a procedure parameter.")
        );
    }

    static Stream<Arguments> negativeCases() {
        return Stream.of(
                of("a = Hello, World!`;", "Omitted ` on start is ignored."),
                of("a = `Hello, World!;", "Omitted ` in end is ignored."),
                of("print(2 + 2 + true);",
                   "String expression without strings is accepted.")
        );
    }
}
