package meador;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;

public class DataStructureTest extends MeadorTest {

    static Stream<Arguments> positiveCases() {
        return Stream.of(
                of("""
                        Point { x, y };
                        print(450);
                        """, "[450.0]", "Data structure is not recognized."),
                of("""
                            Point { x, y };
                            a = Point{1, 2};
                            print(a.y);
                        """, "[2.0]", "Structure's field is not accessible."),
                of("""
                            Point { x, y };
                            a = Point{1, 2};
                            b = Point{2, 1};
                            print(a.y + b.x);
                        """, "[4.0]", "Structures' field values are not interpreted in a proper way.")
        );
    }

    static Stream<Arguments> negativeCases() {
        return Stream.of(
                of("""
                        Point { x, y }
                        print(450);
                        """, "Semicolon absence was ignored."),
                of("""
                        Point { x, y };
                        a = Point{0, 0};
                        print(a.z);
                        """, "Invalid field name call was ignored.")
        );
    }
}
