package aoc.y2022;

import java.util.Set;

import com.google.common.collect.Sets;

import org.junit.jupiter.api.Test;

import utils.AdventOfCode;
import utils.Input;

import static com.google.common.base.Preconditions.checkState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;


@AdventOfCode(year = 2022, day = 3, name = "Rucksack Reorganization")
public class Day03Solution {

  public static final String MOCK = """
      vJrwpWtwJgWrhcsFMMfFFhFp
      jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
      PmmdzqPrVvPwwTWBwg
      wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
      ttgJtRGJQctTZtZT
      CrZsJsPPZsGzwwsLwLmpwMDw
      """;

  @Test
  public void part1WithMockData() {
    assertEquals(157, solutionForPart1(mockInput(MOCK)));
  }

  @Test
  public void part1() {
    assertEquals(8105, solutionForPart1(input(this)));
  }

  @Test
  public void part2WithMockData() {
    assertEquals(70, solutionForPart2(mockInput(MOCK)));
  }

  @Test
  public void part2() {
    assertEquals(2363, solutionForPart2(input(this)));
  }

  private int solutionForPart1(final Input input) {
    var sum = 0;
    for (final String line : input.lines()) {

      final var one = unique(line.substring(0, line.length() / 2));
      final var two = unique(line.substring(line.length() / 2));

      final var intersection = Sets.intersection(one, two);
      checkState(intersection.size() == 1);

      sum += priority(intersection.iterator().next());
    }

    return sum;
  }

  private int solutionForPart2(final Input input) {
    var sum = 0;
    final var lines = input.linesArray();
    for (int i = 0; i < lines.length / 3; i++) {
      var s1 = unique(lines[i * 3]);
      var s2 = unique(lines[(i * 3) + 1]);
      var s3 = unique(lines[(i * 3) + 2]);

      var intersection = Sets.intersection(Sets.intersection(s1, s2), s3);
      sum += priority(intersection.iterator().next());
    }

    return sum;
  }

  private int priority(final Character c) {
    if (c >= 'a' && c <= 'z') {
      return c - 'a' + 1;
    } else {
      return c - 'A' + 27;
    }
  }

  private Set<Character> unique(final String string) {
    final Set<Character> chars = Sets.newHashSet();
    for (final char c : string.toCharArray()) {
      chars.add(c);
    }
    return chars;
  }
}
