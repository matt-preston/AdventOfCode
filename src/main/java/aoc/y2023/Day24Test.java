package aoc.y2023;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import utils.AdventOfCode;
import utils.Input;
import utils.Vector3;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AdventOfCode(year = 2023, day = 24)
public class Day24Test {

  record Hailstone(Vector3 position, Vector3 vector) {}

  @Test
  public void part1WithMockData() {
    var input = Input.mockInput("""
            19, 13, 30 @ -2,  1, -2
            18, 19, 22 @ -1, -1, -2
            20, 25, 34 @ -2, -2, -4
            12, 31, 28 @ -1, -2, -1
            20, 19, 15 @  1, -5, -3
            """);

    assertEquals(2, sumPairsIntersectIn(input, 7, 27));
  }

  @Test
  public void part() {
    var input = Input.input(this);
    assertEquals(15_107, sumPairsIntersectIn(input, 200_000_000_000_000d, 400_000_000_000_000d));
  }

  private int sumPairsIntersectIn(Input input, double min, double max) {
    var hailstones = parse(input);
    var pairs = Sets.combinations(hailstones, 2);

    var sum = 0;

    for (Set<Hailstone> pair : pairs) {
      var iterator = pair.iterator();
      Hailstone h1 = iterator.next();
      Hailstone h2 = iterator.next();

      var l1 = lineSegment(h1, max);
      var l2 = lineSegment(h2, max);

      Coordinate intersection = l1.intersection(l2);
      if (intersection != null) {
        if (intersection.x >= min && intersection.x <= max &&
                intersection.y >= min && intersection.y <= max) {
          sum++;
        }
      }
    }
    return sum;
  }

  private LineSegment lineSegment(Hailstone h, double factor) {
    double toX = h.position.x() + (h.vector().x() * factor);
    double toY = h.position.y() + (h.vector().y() * factor);
    return new LineSegment(h.position.x(), h.position.y(), toX, toY);
  }

  private Set<Hailstone> parse(Input input) {
    var result = Sets.<Hailstone>newHashSet();
    for (String line : input.lines()) {
      String[] parts = line.split("@");
      var p = parts[0].trim().split(",\\s+");
      var v = parts[1].trim().split(",\\s+");
      result.add(new Hailstone(
              new Vector3(Long.parseLong(p[0]), Long.parseLong(p[1]), Long.parseLong(p[2])),
              new Vector3(Long.parseLong(v[0]), Long.parseLong(v[1]), Long.parseLong(v[2]))
      ));
    }
    return result;
  }

}
