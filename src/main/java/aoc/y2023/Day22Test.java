package aoc.y2023;

import com.google.common.collect.*;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineSegment;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;
import utils.Vector3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;

import static java.lang.Math.max;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2023, day = 22)
public class Day22Test {

  record Brick(String name, Vector3 v1, Vector3 v2) {

    public Brick translate(Vector3 vector) {
      return new Brick(name, v1.translate(vector), v2.translate(vector));
    }

    public int x(IntBinaryOperator op) {
      return op.applyAsInt((int) v1.x(), (int) v2.x());
    }

    public int y(IntBinaryOperator op) {
      return op.applyAsInt((int) v1.y(), (int) v2.y());
    }

    public int z(IntBinaryOperator op) {
      return op.applyAsInt((int) v1.z(), (int) v2.z());
    }

    public boolean intersectsInXY(Brick other) {
      var ls1 = new LineSegment(
              new Coordinate(this.v1.x(), this.v1.y()),
              new Coordinate(this.v2.x(), this.v2.y())
      );
      var ls2 = new LineSegment(
              new Coordinate(other.v1.x(), other.v1.y()),
              new Coordinate(other.v2.x(), other.v2.y())
      );
      return ls1.intersection(ls2) != null;
    }
  }

  @Test
  public void part1WithMockData() {
    assertEquals(5, numberOfRemovableBricks(mockInput("""
            1,0,1~1,2,1
            0,0,2~2,0,2
            0,2,3~2,2,3
            0,0,4~0,2,4
            2,0,5~2,2,5
            0,1,6~2,1,6
            1,1,8~1,1,9
            """)));
  }

  @Test
  public void part1() {
    assertEquals(503, numberOfRemovableBricks(input(this)));
  }

  @Test
  public void part2WithMockData() {
    assertEquals(7, sumOfFallingBricks(mockInput("""
            1,0,1~1,2,1
            0,0,2~2,0,2
            0,2,3~2,2,3
            0,0,4~0,2,4
            2,0,5~2,2,5
            0,1,6~2,1,6
            1,1,8~1,1,9
            """)));
  }

  @Test
  public void part2() {
    assertEquals(98431, sumOfFallingBricks(input(this)));
  }

  private int numberOfRemovableBricks(Input input) {
    var bricks = drop(bricks(input));

    var belowMap = belowMap(bricks);
    var aboveMap = aboveMap(bricks);

    var count = 0;
    for (Brick brick : bricks) {
      var removable = true;
      for (Brick above : aboveMap.get(brick)) {
        var below = belowMap.get(above);
        if (below.size() < 2) {
          removable = false;
        }
      }
      if (removable) {
        count++;
      }
    }

    return count;
  }

  private int sumOfFallingBricks(Input input) {
    var bricks = drop(bricks(input));

    var belowMap = belowMap(bricks);
    var aboveMap = aboveMap(bricks);

    var sum = 0;

    for (Brick brick : bricks) {
      var queue = Lists.<Brick>newLinkedList();
      queue.add(brick);

      var falling = Sets.newHashSet();
      falling.add(brick);

      while(!queue.isEmpty()) {
        var next = queue.poll();
        for (Brick above : aboveMap.get(next)) {
          if (falling.containsAll(belowMap.get(above))) {
            queue.add(above);
            falling.add(above);
          }
        }
      }
      sum += falling.size() - 1;
    }

    return sum;
  }

  private Multimap<Brick, Brick> aboveMap(Collection<Brick> bricks) {
    var result = HashMultimap.<Brick, Brick>create();
    for (Brick brick : bricks) {
      result.putAll(brick, bricks.stream()
              .filter(other -> other.z(Math::min) == brick.z(Math::max) + 1)
              .filter(brick::intersectsInXY)
              .toList());
    }
    return result;
  }

  private Multimap<Brick, Brick> belowMap(Collection<Brick> bricks) {
    var result = HashMultimap.<Brick, Brick>create();
    for (Brick brick : bricks) {
      result.putAll(brick, bricks.stream()
              .filter(other -> other.z(Math::max) == brick.z(Math::min) - 1)
              .filter(brick::intersectsInXY)
              .toList());
    }
    return result;
  }

  private List<Brick> drop(final List<Brick> bricks) {
    // initialised to 0 == ground
    var depthMatrix = new int[maxDimension(bricks, v -> (int) v.x()) + 1][maxDimension(bricks, v -> (int) v.y()) + 1];

    // Sort bricks by minimum z
    bricks.sort((b1, b2) -> ComparisonChain.start()
            .compare(b1.z(Math::min), b2.z(Math::min))
            .compare(b1.x(Math::min), b2.x(Math::min))
            .compare(b1.y(Math::min), b2.y(Math::min))
            .result());

    var result = Lists.<Brick>newArrayList();

    // Drop bricks in order
    for (Brick brick : bricks) {
      var max = 0;
      for (int x = brick.x(Math::min); x < brick.x(Math::max) + 1; x++) {
        for (int y = brick.y(Math::min); y < brick.y(Math::max) + 1; y++) {
          max = max(max, depthMatrix[x][y]);
        }
      }

      var minZ = brick.z(Math::min);
      var maxZ = brick.z(Math::max);

      var dropVector = new Vector3(0, 0, -1 * (minZ - (max + 1)));

      // Update depth buffer
      int updateTo = (maxZ - minZ) + max + 1;

      for (int x = brick.x(Math::min); x < brick.x(Math::max) + 1; x++) {
        for (int y = brick.y(Math::min); y < brick.y(Math::max) + 1; y++) {
          depthMatrix[x][y] = updateTo;
        }
      }

      result.add(brick.translate(dropVector));
    }

    return result;
  }

  private List<Brick> bricks(Input input) {
    final var result = new ArrayList<Brick>();
    char name = 'A';
    for (final String line : input.lines()) {
      var parts = line.split("~");

      final var v1 = Utils.parseNumbers(parts[0], ",");
      final var v2 = Utils.parseNumbers(parts[1], ",");
      result.add(new Brick(
              String.valueOf(name++),
              new Vector3(v1.get(0).intValue(), v1.get(1).intValue(), v1.get(2).intValue()),
              new Vector3(v2.get(0).intValue(), v2.get(1).intValue(), v2.get(2).intValue())
      ));
    }
    return result;
  }

  private int maxDimension(final Collection<Brick> bricks, final Function<Vector3, Integer> fun) {
    return bricks.stream()
            .mapToInt(b -> max(fun.apply(b.v1), fun.apply(b.v2)))
            .max()
            .orElse(0);
  }
}
