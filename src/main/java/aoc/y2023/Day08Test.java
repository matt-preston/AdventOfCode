package aoc.y2023;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.commons.math3.util.ArithmeticUtils;
import org.junit.jupiter.api.Test;

import utils.AdventOfCode;
import utils.Input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2023, day = 8)
public class Day08Test {

  @Test
  public void part1WithMockData() {
    assertEquals(2, solutionForPart1(mockInput("""
        RL

        AAA = (BBB, CCC)
        BBB = (DDD, EEE)
        CCC = (ZZZ, GGG)
        DDD = (DDD, DDD)
        EEE = (EEE, EEE)
        GGG = (GGG, GGG)
        ZZZ = (ZZZ, ZZZ)
        """)));
  }

  @Test
  public void part1() {
    assertEquals(22411, solutionForPart1(input(this)));
  }

  @Test
  public void part2WithMockData() {
    assertEquals(6, solutionForPart2(mockInput("""
        LR
                
        11A = (11B, XXX)
        11B = (XXX, 11Z)
        11Z = (11B, XXX)
        22A = (22B, XXX)
        22B = (22C, 22C)
        22C = (22Z, 22Z)
        22Z = (22B, 22B)
        XXX = (XXX, XXX)
        """)));
  }

  @Test
  public void part2() {
    assertEquals(11188774513823L, solutionForPart2(input(this)));
  }

  private long solutionForPart1(final Input input) {
    var iter = sequenceIterator(input);
    var map = nodeMap(input);

    var next = "AAA";
    var steps = 0;
    do {
      next = map.get(next).get(iter.next());
      steps++;
    } while (!next.equals("ZZZ"));

    return steps;
  }

  private long solutionForPart2(final Input input) {
    var map = nodeMap(input);
    var startingNodes = map.keySet().stream().filter(k -> k.endsWith("A")).sorted().toList();

    var periods = Lists.<Long>newArrayList();

    for (final String node : startingNodes) {
      var iter = sequenceIterator(input);

      var next = node;
      var steps = 0L;
      do {
        next = map.get(next).get(iter.next());
        steps++;
      } while (!next.endsWith("Z"));

      periods.add(steps);
    }

    return periods.stream()
        .mapToLong(l -> l)
        .reduce(1, ArithmeticUtils::lcm);
  }

  private Iterator<Character> sequenceIterator(final Input input) {
    final var sequence = input.linesArray()[0].chars().mapToObj(c -> (char) c).toArray(Character[]::new);
    return Iterators.cycle(sequence);
  }

  private Map<String, Map<Character, String>> nodeMap(final Input input) {
    var map = Maps.<String, Map<Character, String>>newLinkedHashMap();

    final var lines = input.lines();
    for (int i = 2; i < lines.size(); i++) {
      var line = lines.get(i);
      final var matcher = Pattern.compile("(\\w{3}) = \\((\\w{3}), (\\w{3})\\)").matcher(line);
      Preconditions.checkState(matcher.matches());
      map.put(matcher.group(1), Map.of('L', matcher.group(2), 'R', matcher.group(3)));
    }

    return map;
  }
}
