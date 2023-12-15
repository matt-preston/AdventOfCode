package aoc.y2023;

import java.util.List;
import java.util.TreeMap;

import com.google.common.collect.Maps;

import org.junit.jupiter.api.Test;

import utils.AdventOfCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;

@AdventOfCode(year = 2023, day = 1)
public class Day01Test {

  private static final List<String> DIGITS = List.of(
      "1", "2", "3", "4", "5", "6", "7", "8", "9"
  );

  private static final List<String> DIGITS_WORDS = List.of(
      "1", "2", "3", "4", "5", "6", "7", "8", "9", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
  );

  @Test
  public void part1() {
    var total = 0;
    for (final String line : input(this).lines()) {
      total += (valueOf(firstDigit(line, DIGITS)) * 10) + valueOf(lastDigit(line, DIGITS));
    }
    assertEquals(54940, total);
  }

  @Test
  public void part2() {
    var total = 0;
    for (final String line : input(this).lines()) {
      total += (valueOf(firstDigit(line, DIGITS_WORDS)) * 10) + valueOf(lastDigit(line, DIGITS_WORDS));
    }
    assertEquals(54208, total);
  }

  private String firstDigit(final String input, final List<String> numbers) {
    final TreeMap<Integer, String> result = Maps.newTreeMap();
    for (final String match : numbers) {
      final var index = input.indexOf(match);
      if (index > -1) {
        result.put(index, match);
      }
    }
    return result.firstEntry().getValue();
  }

  private String lastDigit(final String input, final List<String> numbers) {
    final TreeMap<Integer, String> result = Maps.newTreeMap();
    for (final String match : numbers) {
      final var index = input.lastIndexOf(match);
      if (index > -1) {
        result.put(index, match);
      }
    }
    return result.lastEntry().getValue();
  }

  private int valueOf(final String digit) {
    return switch (digit) {
      case "one" -> 1;
      case "two" -> 2;
      case "three" -> 3;
      case "four" -> 4;
      case "five" -> 5;
      case "six" -> 6;
      case "seven" -> 7;
      case "eight" -> 8;
      case "nine" -> 9;
      default -> Integer.parseInt(digit);
    };
  }

}
