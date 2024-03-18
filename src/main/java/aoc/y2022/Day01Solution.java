package aoc.y2022;

import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;

import org.junit.jupiter.api.Test;

import utils.AdventOfCode;
import utils.Input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;


@AdventOfCode(year = 2022, day = 1, name = "Calorie Counting")
public class Day01Solution {

  @Test
  public void part1() {
    var elves = elves(input(this));
    assertEquals(67658, elves.get(0).intValue());
  }

  @Test
  public void part2() {
    var elves = elves(input(this));
    assertEquals(200158, (elves.get(0) + elves.get(1) + elves.get(2)));
  }

  private List<Integer> elves(final Input input) {
    final List<Integer> elves = Lists.newArrayList();

    int calories = 0;
    for (final String string : input.lines()) {
      if (string.trim().isEmpty()) {
        elves.add(calories);
        calories = 0;
      } else {
        calories += Integer.parseInt(string.trim());
      }
    }

    if (calories > 0) {
      elves.add(calories);
    }

    elves.sort(Comparator.reverseOrder());
    return elves;
  }
}
