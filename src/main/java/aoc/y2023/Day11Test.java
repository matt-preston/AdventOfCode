package aoc.y2023;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.junit.jupiter.api.Test;

import utils.AdventOfCode;
import utils.Input;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2023, day = 11)
public class Day11Test {

  record Point(int x, int y) {

    int distance(Point o) {
      return Math.abs(x - o.x) + Math.abs(y - o.y);
    }
  }

  @Test
  public void part1WithMockData() {
    assertEquals(374, sumOfShortestPaths(mockInput("""
        ...#......
        .......#..
        #.........
        ..........
        ......#...
        .#........
        .........#
        ..........
        .......#..
        #...#.....
        """)));
  }

  @Test
  public void part1() {
    assertEquals(9627977, sumOfShortestPaths(input(this)));
  }

  @Test
  public void part2WithMockDataTimes10() {
    assertEquals(1030, sumOfShortestPaths(mockInput("""
        ...#......
        .......#..
        #.........
        ..........
        ......#...
        .#........
        .........#
        ..........
        .......#..
        #...#.....
        """), 10));
  }

  @Test
  public void part2WithMockDataTimes100() {
    assertEquals(8410, sumOfShortestPaths(mockInput("""
        ...#......
        .......#..
        #.........
        ..........
        ......#...
        .#........
        .........#
        ..........
        .......#..
        #...#.....
        """), 100));
  }

  @Test
  public void part2() {
    assertEquals(644248339497L, sumOfShortestPaths(input(this), 1_000_000));
  }

  private long sumOfShortestPaths(final Input input) {
    return sumOfShortestPaths(input, 2);
  }

  private long sumOfShortestPaths(final Input input, int weightOfEmpty) {
    final var pointMap = plotGalaxies(input.lines(), weightOfEmpty);

    for (final Map.Entry<Integer, Point> entry : pointMap.entrySet()) {
      System.out.println(entry);
    }

    // Find all {point, point} combinations
    final var pairs = Sets.combinations(IntStream.range(1, pointMap.size() + 1)
        .boxed()
        .collect(toSet()), 2);

    // sum distance between each pair of points
    return pairs.stream()
        .map(p -> p.toArray(Integer[]::new))
        .mapToLong(p -> pointMap.get(p[0]).distance(pointMap.get(p[1])))
        .sum();
  }

  private Map<Integer, Point> plotGalaxies(final List<String> universe, int weightOfEmpty) {
    var result = Maps.<Integer, Point>newHashMap();
    var count = 1;

    for (int y = 0; y < universe.size(); y++) {
      final var row = universe.get(y);
      int index = 0;
      while (index != -1) {
        index = row.indexOf('#', index);
        if (index != -1) {
          var x = index++;

          final var emptyColumns = numberOfPrecedingEmptyColumns(x, universe);
          final var emptyRows = numberOfPrecedingEmptyRows(y, universe);

          var point = new Point(
              x + (emptyColumns * weightOfEmpty) - emptyColumns,
              y + (emptyRows * weightOfEmpty) - emptyRows
          );

          result.put(count++, point);
        }
      }
    }

    return result;
  }

  private int numberOfPrecedingEmptyRows(int row, final List<String> universe) {
    var count = 0;
    for (int i = 0; i < row; i++) {
      if (universe.get(i).matches("\\.+")) {
        count++;
      }
    }
    return count;
  }

  private int numberOfPrecedingEmptyColumns(int column, final List<String> universe) {
    var count = 0;
    for (int i = 0; i < column; i++) {
      boolean emptyColumn = true;
      for (final String row : universe) {
        emptyColumn = emptyColumn && empty(row, i);
      }
      if (emptyColumn) {
        count++;
      }
    }

    return count;
  }

  private boolean empty(String string, int index) {
    return string.charAt(index) == '.';
  }
}
