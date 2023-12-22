package aoc.y2023;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.IntStream;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableSortedSet;

import org.checkerframework.common.value.qual.IntRange;
import org.junit.jupiter.api.Test;

import utils.Input;
import utils.Utils;

public class Day22Test {

  record Vector3(int x, int y, int z) {

    @Override
    public String toString() {
      return "(" + x + "," + y + "," + z + ")";
    }
  }

  record Brick(String name, Vector3 v1, Vector3 v2) implements Comparable<Brick> {

    public boolean intersectsZ(int z) {
      return Math.min(v1.z, v2.z) <= z && Math.max(v1.z, v2.z) >= z;
    }

    public boolean intersectsX(int x) {
      return Math.min(v1.x, v2.x) <= x && Math.max(v1.x, v2.x) >= x;
    }

    public boolean intersectsY(int y) {
      return Math.min(v1.y, v2.y) <= y && Math.max(v1.y, v2.y) >= y;
    }

    @Override
    public int compareTo(final Brick o) {
      return ComparisonChain.start()
          .compare(Math.max(v1.z(), v2.x()), Math.max(o.v1.z(), o.v2.x()))
          .result();
    }
  }

  @Test
  public void part1WithMockData() {
    final var bricks = bricks(Input.mockInput("""
        1,0,1~1,2,1
        0,0,2~2,0,2
        0,2,3~2,2,3
        0,0,4~0,2,4
        2,0,5~2,2,5
        0,1,6~2,1,6
        1,1,8~1,1,9
        """));

    debugInX(bricks);
    System.out.println();
    debugInY(bricks);

    var updated = drop(bricks);
    debugInX(updated);
    System.out.println();
    debugInY(updated);
  }

  private SortedSet<Brick> drop(final SortedSet<Brick> bricks) {
    // initialised to 0 == ground
    var depthMatrix = new char[maxDimension(bricks, Vector3::x)][maxDimension(bricks, Vector3::y)];
//    for (int x = 0; x < depthMatrix.length; x++) {
//      for (int y = 0; y < depthMatrix[0].length; y++) {
//        depthMatrix
//      }
//    }

    // sort bricks by min(v1.z, v2.z) so that they are in order of closest to the ground
    // keep track of max z for (x,y) from ground up
    // iterating bricks, reduce z by difference in min(z) to max(z) from ground
    // done

    return ImmutableSortedSet.copyOf(bricks);
  }

  private SortedSet<Brick> bricks(Input input) {
    final var result = new TreeSet<Brick>();
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

  private void debugInX(Collection<Brick> bricks) {
    var maxX = maxDimension(bricks, Vector3::x);
    var maxZ = maxDimension(bricks, Vector3::z);

    System.out.println(" x ");
    IntStream.range(0, maxX + 1).forEach(System.out::print);
    System.out.println();

    for (int z = maxZ; z > 0; z--) {
      final int finalZ = z;
      var onZ = bricks.stream().filter(b -> b.intersectsZ(finalZ)).toList();
      for (int x = 0; x < maxX + 1; x++) {
        final int finalX = x;
        final var list = onZ.stream().filter(b -> b.intersectsX(finalX)).toList();
        if (list.isEmpty()) {
          System.out.print(".");
        } else if (list.size() > 1) {
          System.out.print('?');
        } else {
          System.out.print(list.get(0).name);
        }
      }
      System.out.println(" " + z);
    }
    System.out.println("-".repeat(maxX + 1) + " 0");
  }

  private void debugInY(Collection<Brick> bricks) {
    var maxY = maxDimension(bricks, Vector3::y);
    var maxZ = maxDimension(bricks, Vector3::z);

    System.out.println(" y ");
    IntStream.range(0, maxY + 1).forEach(System.out::print);
    System.out.println();

    for (int z = maxZ; z > 0; z--) {
      final int finalZ = z;
      var onZ = bricks.stream().filter(b -> b.intersectsZ(finalZ)).toList();
      for (int y = 0; y < maxY + 1; y++) {
        final int finalY = y;
        final var list = onZ.stream().filter(b -> b.intersectsY(finalY)).toList();
        if (list.isEmpty()) {
          System.out.print(".");
        } else if (list.size() > 1) {
          System.out.print('?');
        } else {
          System.out.print(list.get(0).name);
        }
      }
      System.out.println(" " + z);
    }
    System.out.println("-".repeat(maxY + 1) + " 0");
  }

  private int maxDimension(final Collection<Brick> bricks, final Function<Vector3, Integer> fun) {
    return bricks.stream()
        .mapToInt(b -> Math.max(fun.apply(b.v1), fun.apply(b.v2)))
        .max()
        .orElse(0);
  }
}
