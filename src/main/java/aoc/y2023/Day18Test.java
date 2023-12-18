package aoc.y2023;

import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.algorithm.Area;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import utils.AdventOfCode;
import utils.Input;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2023, day = 18)
public class Day18Test {

  final static GeometryFactory FACTORY = new GeometryFactory();

  @Test
  public void part1WithMockData() {
    assertEquals(62, part1Area(mockInput("""
        R 6 (#70c710)
        D 5 (#0dc571)
        L 2 (#5713f0)
        D 2 (#d2c081)
        R 2 (#59c680)
        D 2 (#411b91)
        L 5 (#8ceee2)
        U 2 (#caa173)
        L 1 (#1b58a2)
        U 2 (#caa171)
        R 2 (#7807d2)
        U 3 (#a77fa3)
        L 2 (#015232)
        U 2 (#7a21e3)
        """)));
  }

  @Test
  public void part1() {
    assertEquals(62573, part1Area(input(this)));
  }

  @Test
  public void part2WithMockData() {
    assertEquals(952408144115L, part2Area(mockInput("""
        R 6 (#70c710)
        D 5 (#0dc571)
        L 2 (#5713f0)
        D 2 (#d2c081)
        R 2 (#59c680)
        D 2 (#411b91)
        L 5 (#8ceee2)
        U 2 (#caa173)
        L 1 (#1b58a2)
        U 2 (#caa171)
        R 2 (#7807d2)
        U 3 (#a77fa3)
        L 2 (#015232)
        U 2 (#7a21e3)
        """)));
  }

  @Test
  public void part2() {
    assertEquals(54662804037719L, part2Area(input(this)));
  }

  private Coordinate c(double x, double y) {
    return new Coordinate(x, y);
  }

  @Test
  public void testPolygonMaths() {
    final var p1 = polygon(c(0, 0), c(2, 0));
    final var p2 = polygon(c(2, 0), c(2, 2));
    final var p3 = polygon(c(2, 2), c(0, 2));
    final var p4 = polygon(c(0, 2), c(0, 0));

    assertEquals("POLYGON ((0 0, 3 0, 3 1, 0 1, 0 0))", p1.toText());
    assertEquals("POLYGON ((2 0, 3 0, 3 3, 2 3, 2 0))", p2.toText());
    assertEquals("POLYGON ((0 2, 3 2, 3 3, 0 3, 0 2))", p3.toText());
    assertEquals("POLYGON ((0 0, 1 0, 1 3, 0 3, 0 0))", p4.toText());

    assertEquals(9, p1.union(p2.union(p3.union(p4))).getEnvelope().getArea());
  }

  private long part1Area(final Input input) {
    return area(
        input,
        line -> line.split("\\s", 3)[0],
        line -> Integer.parseInt(line.split("\\s", 3)[1])
    );
  }

  private long part2Area(final Input input) {
    final var directionMap = ImmutableMap.of(
        "0", "R",
        "1", "D",
        "2", "L",
        "3", "U"
    );
    return area(
        input,
        line -> {
          final var key = String.valueOf(line.charAt(line.length() - 2));
          return directionMap.getOrDefault(key, "?");
        },
        line -> {
          final var substring = line.split("#", 2)[1].substring(0, 5);
          return Integer.parseInt(substring, 16);
        }
    );
  }

  private long area(final Input input, Function<String, String> directionFun, Function<String, Integer> distanceFun) {
    var polygon = FACTORY.createPolygon();
    var previousGridLocation = c(0, 0);

    for (final String line : input.lines()) {
      var direction = directionFun.apply(line);
      var distance = distanceFun.apply(line);

      var nextGridLocation = switch (direction) {
        case "R" -> c(previousGridLocation.x + distance, previousGridLocation.y);
        case "L" -> c(previousGridLocation.x - distance, previousGridLocation.y);
        case "U" -> c(previousGridLocation.x, previousGridLocation.y - distance);
        case "D" -> c(previousGridLocation.x, previousGridLocation.y + distance);
        default -> throw new IllegalStateException();
      };

      polygon = (Polygon) polygon.union(polygon(previousGridLocation, nextGridLocation));
      previousGridLocation = nextGridLocation;
    }

    return (long) Area.ofRing(polygon.getExteriorRing().getCoordinates());
  }

  private Polygon polygon(final Coordinate from, final Coordinate to) {
    return FACTORY.createPolygon(new Coordinate[] {
        c(min(from.x, to.x), min(from.y, to.y)),
        c(max(from.x, to.x) + 1, min(from.y, to.y)),
        c(max(from.x, to.x) + 1, max(from.y, to.y) + 1),
        c(min(from.x, to.x), max(from.y, to.y) + 1),
        c(min(from.x, to.x), min(from.y, to.y))
    });
  }
}
