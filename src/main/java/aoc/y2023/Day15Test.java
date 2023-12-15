package aoc.y2023;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import utils.AdventOfCode;
import utils.Input;

import static com.google.common.base.Preconditions.checkState;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2023, day = 15)
public class Day15Test {

  record Box(int index, LinkedHashMap<String, Integer> lenses){}

  @Test
  public void part1WithMockData() {
    assertEquals(1320, sumOfHashValues(mockInput("""
        rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7
        """)));
  }

  @Test
  public void part1() {
    assertEquals(512797, sumOfHashValues(input(this)));
  }

  @Test
  public void part2WithMockData() {
    assertEquals(145, focusingPower(mockInput("""
        rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7
        """)));
  }

  @Test
  public void part2() {
    assertEquals(262454, focusingPower(input(this)));
  }

  private int sumOfHashValues(final Input input) {
    return Arrays.stream(input.text().split(","))
        .map(String::trim)
        .mapToInt(this::hash)
        .sum();
  }

  private int hash(final String str) {
    return str.chars()
        .reduce(0, (current, next) -> (17 * (current + next)) % 256);
  }

  private int focusingPower(Input input) {
    final var boxes = new Box[256];
    for (int i = 0; i < boxes.length; i++) {
      boxes[i] = new Box(i, new LinkedHashMap<>());
    }

    // initialise boxes
    Arrays.stream(input.text().split(","))
        .map(String::trim)
        .forEach(s -> step(boxes, s));

    var sum = 0;

    for (final Box box : boxes) {
      if (!box.lenses.isEmpty()) {
        var lensIndex = 0;
        for (final Map.Entry<String, Integer> entry : box.lenses.entrySet()) {
          sum += ((box.index + 1) * ++lensIndex * entry.getValue());
        }
      }
    }

    return sum;
  }

  private void step(Box[] boxes, String step) {
    final var pattern = Pattern.compile("(\\w+)(-|=)(\\d?)");
    final var matcher = pattern.matcher(step);
    checkState(matcher.matches());

    var label = matcher.group(1);
    var op = matcher.group(2);
    var box = boxes[hash(matcher.group(1))];

    switch(op) {
      case "=" -> box.lenses.put(label, Integer.parseInt(matcher.group(3)));
      case "-" -> box.lenses.remove(label);
    }
  }
}
