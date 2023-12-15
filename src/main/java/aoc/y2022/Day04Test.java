package aoc.y2022;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import utils.AdventOfCode;
import utils.Input;

import static com.google.common.base.Preconditions.checkState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2022, day = 4)
public class Day04Test {

  public static final String MOCK = """
      2-4,6-8
      2-3,4-5
      5-7,7-9
      2-8,3-7
      6-6,4-6
      2-6,4-8
      """;


  record Range(int from, int to) {

    public boolean contains(final Range other) {
      return other.from >= from && other.to <= to;
    }

    public boolean overlaps(final Range other) {
      return other.from >= from && other.from <= to;
    }
  }

  @Test
  public void part1WithMockData() {
    assertEquals(2, solutionForPart1(mockInput(MOCK)));
  }

  @Test
  public void part1() {
    assertEquals(456, solutionForPart1(input(this)));
  }

  @Test
  public void part2WithMockData() {
    assertEquals(4, solutionForPart2(mockInput(MOCK)));
  }

  @Test
  public void part2() {
    assertEquals(808, solutionForPart2(input(this)));
  }

  private int solutionForPart1(final Input input) {
    var pattern = Pattern.compile("(\\d+)-(\\d+),(\\d+)-(\\d+)");

    var count = 0;
    for (final String line : input.lines()) {
      final var matcher = pattern.matcher(line);
      checkState(matcher.find());

      var s1 = new Range(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
      var s2 = new Range(Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));

      if (s1.contains(s2) || s2.contains(s1)) {
        count++;
      }
    }

    return count;
  }

  private int solutionForPart2(final Input input) {
    var pattern = Pattern.compile("(\\d+)-(\\d+),(\\d+)-(\\d+)");

    var count = 0;
    for (final String line : input.lines()) {
      final var matcher = pattern.matcher(line);
      checkState(matcher.find());

      var s1 = new Range(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
      var s2 = new Range(Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));

      if (s1.overlaps(s2) || s2.overlaps(s1)) {
        count++;
      }
    }

    return count;
  }

}
