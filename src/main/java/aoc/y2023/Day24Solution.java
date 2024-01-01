package aoc.y2023;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import utils.AdventOfCode;
import utils.Input;
import utils.Vector3;

import java.util.Set;

import static com.google.common.collect.Iterators.limit;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AdventOfCode(year = 2023, day = 24, name = "Never Tell Me The Odds")
public class Day24Solution {

  public static final String MOCK = """
          19, 13, 30 @ -2,  1, -2
          18, 19, 22 @ -1, -1, -2
          20, 25, 34 @ -2, -2, -4
          12, 31, 28 @ -1, -2, -1
          20, 19, 15 @  1, -5, -3
          """;

  record Hailstone(Vector3 start, Vector3 velocity) {}

  @Test
  public void part1WithMockData() {
    var input = Input.mockInput(MOCK);
    assertEquals(2, sumPairsIntersectIn(input, 7, 27));
  }

  @Test
  public void part1() {
    var input = Input.input(this);
    assertEquals(15_107, sumPairsIntersectIn(input, 200_000_000_000_000d, 400_000_000_000_000d));
  }

  @Test
  public void part2WithMockData() {
    var input = Input.mockInput("""
            19, 13, 30 @ -2,  1, -2
            18, 19, 22 @ -1, -1, -2
            20, 25, 34 @ -2, -2, -4
            12, 31, 28 @ -1, -2, -1
            20, 19, 15 @  1, -5, -3
            """);

    generateSageMath(input);

    // x + y + z == 47
  }

  @Test
  public void part2() {
    var input = Input.input(this);
    generateSageMath(input);

    // x + y + z == 856642398547748
  }

  // https://sagecell.sagemath.org/
  private void generateSageMath(Input input) {
    System.out.println("x = var('x')");
    System.out.println("y = var('y')");
    System.out.println("z = var('z')");
    System.out.println("vx = var('vx')");
    System.out.println("vy = var('vy')");
    System.out.println("vz = var('vz')");
    System.out.println("t1 = var('t1')");
    System.out.println("t2 = var('t2')");
    System.out.println("t3 = var('t3')");

    var iter = limit(parse(input).iterator(), 3);
    var eqCount = 1;
    var tCount = 1;
    while(iter.hasNext()) {
      var hailstone = iter.next();
      // These equations can be simplified...
      System.out.printf("eq%d = x + (vx*t%d) == %d + (%d * t%d)%n", eqCount++, tCount, hailstone.start.x(), hailstone.velocity().x(), tCount);
      System.out.printf("eq%d = y + (vy*t%d) == %d + (%d * t%d)%n", eqCount++, tCount, hailstone.start.y(), hailstone.velocity().y(), tCount);
      System.out.printf("eq%d = z + (vz*t%d) == %d + (%d * t%d)%n", eqCount++, tCount, hailstone.start.z(), hailstone.velocity().z(), tCount);
      tCount++;
    }

    System.out.println("solutions = solve([eq1,eq2,eq3,eq4,eq5,eq6,eq7,eq8,eq9],x,y,z,vx,vy,vz,t1,t2,t3)");
    System.out.println("solutions[0][0]+solutions[0][1]+solutions[0][2]");
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
    double toX = h.start.x() + (h.velocity().x() * factor);
    double toY = h.start.y() + (h.velocity().y() * factor);
    return new LineSegment(h.start.x(), h.start.y(), toX, toY);
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
