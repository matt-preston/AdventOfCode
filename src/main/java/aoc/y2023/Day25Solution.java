package aoc.y2023;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2023, day = 25)
public class Day25Solution {

  private static final String MOCK = """
          """;

  @Test
  public void part1WithMockData() {
    assertEquals("", mockInput(MOCK).text());
  }

  @Test
  public void part1() {
    assertEquals("", input(this).text());
  }
}
