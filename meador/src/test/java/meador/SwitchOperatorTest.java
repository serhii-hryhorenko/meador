package meador;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;

public class SwitchOperatorTest extends MeadorTest {

    static Stream<Arguments> positiveCases() {
        return Stream.of(
                of("""
                        a = 5;
                        switch (a) {
                        case 5: { print(a); }
                        case 2: { a = 5; }
                        default: { print(450); }
                        }
                        """, "[5.0]", "Default use case was failed."),
                of("""
                        a = pi();
                        switch (a) {
                        case 0: { flush(); }
                        case pi(): { print(3, 0.14); }
                        default: { print(450); }
                        }
                        """, "[3.0, 0.14]", "Proper case wasn't chosen."),
                of("""
                        a = pi();
                        switch (a) {
                            case 0: { flush(); }
                            default: { print(450); }
                        }
                            """, "[450.0]", "Default case isn't working."),
                of("""
                        a = 10;
                        switch(a) {
                            case 10: { a = 0; }
                            case 0: { print(1); }
                            default: { print(450); }
                        }
                        print(a);
                        """, "[0.0]", "Variable was matched after case option execution."),
                of("""
                        a = pi();
                        switch(a) {
                            case 0: { print(450); }
                            default: { switch (a) {
                                    case a: { a = 451; }
                                    default: { print(1); }
                                }
                            }
                        }
                        print(a);
                        """, "[451.0]", "Nested switch is broken."),
                of("""
                        a = 1 > 2;
                        switch(a) {
                            case 1 < 2: { print(450); }
                            default: { print(1); }
                        }
                        """, "[1.0]", "Boolean matching is broken."),
                of("""
                        a = 1 > 2;
                        switch(a) {
                            case 50: { print(450); }
                            default: { print(a); }
                        }
                        """, "[false]", "Multi-type matching is broken.")
        );
    }

    static Stream<Arguments> negativeCases() {
        return Stream.of(
                of("""
                        a = pi();
                        switch(a) {
                                                
                        }
                        """, "Empty switch operator was accepted."),
                of("""
                        a = pi();
                        switch(a) {
                            case pi(): { print(450); }
                        }
                        """, "Switch without default was accepted."),
                of("""
                        a = pi();
                        switch(a) {
                            default: { print(450); }
                        }
                        """, "Switch without case options was accepted."),

                of("""
                        a = pi();
                        switch(a) {
                            default: { print(450); }
                            case pi(): { print(a); }
                        }
                        """, "Case option after default was accepted."),
                of("""
                        a = pi();
                        switch(a) {
                            case 0: { clear(); }
                            default: { print(450); }
                            case pi(): { print(a); }
                        }
                        """, "Default in the middle of case options was accepted."),
                of("""
                        switch(pi()) {
                            case pi(): { print(a); }
                            case 0: { clear(); }
                            default: { print(450); }
                        }
                        """, "Switch call on a non-variable value was ignored."),
                of("""
                        a = pi();
                        switch(a) {
                            case pi(): { }
                            default: { print(450); }         
                        }
                        """, "", "Case option without expression was not accepted.")

        );
    }
}
