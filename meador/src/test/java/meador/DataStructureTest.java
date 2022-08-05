package meador;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.of;

class DataStructureTest extends MeadorTest {

    static Stream<Arguments> positiveCases() {
        return Stream.of(
                of("""
                               Point { x, y };
                               print(450);
                           """,
                   "[450.0]",
                   "Data structure is not recognized."),

                of("""
                               Point { x, y };
                               a = Point{0, 0};
                               print(a);
                           """,
                   "[Point{fields=[x, y]}]",
                   "Data structure is not recognized."),

                of("""
                               Point { x, y };
                               a = Point{1, 2};
                               print(a.x, a.y);
                               a.x = 3;
                               print(a.x);
                           """,
                   "[1.0, 2.0]" + System.lineSeparator() + "[3.0]",
                   "Structure's field is not accessible."),

                of("""
                               Point { x, y };
                               a = Point{1, 2};
                               a.x = 3;
                               b = a.x;
                               print(b);
                           """,
                   "[3.0]",
                   "Structure's field is not accessible."),

                of("""
                               Point { x, y };
                               a = Point{1, 2};
                               b = Point{2, 1};
                               print(a.y + b.x);
                           """,
                   "[4.0]",
                   "Structures' field values are not interpreted in a proper way as a procedure parameter."),

                of("""
                               Point{x,y};
                               Vector{a, b};
                                                       
                               a = Point{0, -1};
                               v = Vector{1, 0};
                               
                               endpoint = Point{a.x + v.a, a.y + v.b};
                               print(endpoint.x, endpoint.y);
                           """,
                   "[1.0, -1.0]",
                   "Structures' field values are not interpreted in a proper way as a construction parameter."),

                of("""  
                               Point{x,y};
                               a = Point{0, 0};
                               a.x = 1;
                               a.y = -1;
                               print(a.x + a.y);
                           """,
                   "[0.0]",
                   "Field assignment is broken."),

                of("""  
                               pi = pi();
                               Vector{x,y,z};
                               a = Vector{0, 0, 0};
                               a.x = 6;
                               a.y = a.x + pi();
                               a.z = average(a.x, pi);
                               print(a.x + a.y + a.z);
                           """, "[" + (6 + 6 + Math.PI + ((6 + Math.PI) / 2)) + ']',
                   "Field assignment is broken."),

                of("""
                           Point{x,y};
                           a = Point{1, 2};
                           b = a;
                           print(b.y);
                           """,
                   "[2.0]",
                   "Structure reassignment does not work."),

                of("""
                           Point{x,y};
                           a = Point{1 < 2, 2};
                           print(a.x, a.y);
                           """,
                   "[true, 2.0]",
                   "Field weak typing is broken."),

                of("""
                           Point{x,y};
                           a = Point{0, 0};
                           b = Point{a, Point{1, 1}};
                           ap = b.x;
                           ax = ap.x;
                           bp = b.y;
                           by = bp.y;
                           print(ax, by);
                           """,
                   "[0.0, 1.0]",
                   "Structures are not accessible to copy from fields.")
        );
    }

    static Stream<Arguments> negativeCases() {
        return Stream.of(
                of("""
                           Point { x, y }
                           print(450);
                           """,
                   "Semicolon absence was ignored."),

                of("""
                           Point { x, y };
                           a = Point{0, 0};
                           print(a.z);
                           """,
                   "Invalid field name call was ignored."),

                of("""
                           a = Point{1, 2};
                           """,
                   "Structure instantiating was accepted without template declaration."),

                of("""
                           Coordinate{x, y};
                           a = Coordinate{1};
                           """,
                   "Invalid constructor usage was ignored."),

                of("""
                           Coordinate{x, y};
                           a = Coordinate{1, 2, 3};
                           """,
                   "Invalid constructor usage was ignored."),

                of("""
                           Coordinate{x, y};
                           a.x = Coordinate{1, 2, 3};
                           """,
                   "Invalid field call was ignored.")
        );
    }
}
