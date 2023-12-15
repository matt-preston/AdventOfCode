package aoc.y2023;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import utils.AdventOfCode;
import utils.Input;

import static java.lang.Math.max;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2023, day = 14)
public class Day14Test {

  record Cycle(int offset, int length) {}

  public static final String MOCK = """
      O....#....
      O.OO#....#
      .....##...
      OO.#O....O
      .O.....O#.
      O.#..O.#.#
      ..O..#O..O
      .......O..
      #....###..
      #OO..#....
      """;

  @Test
  public void part1WithMockData() {
    assertEquals(136, part1Score(mockInput(MOCK)));
  }

  @Test
  public void part1() {
    assertEquals(108614, part1Score(input(this)));
  }

  @Test
  public void sanityCheckPart2() {
    final var scores = scoreCycle(mockInput(MOCK), 3);
    assertEquals(87, scores[0]);
    assertEquals(69, scores[1]);
    assertEquals(69, scores[2]);
  }

  @Test
  public void part2WithMockData() {
    assertEquals(64, part2Score(mockInput(MOCK)));
  }

  @Test
  public void part2() {
    assertEquals(96447, part2Score(input(this)));
  }

  private int part2Score(Input input) {
    final var scores = scoreCycle(input, 500);
    final var cycle = findCycle(scores);
    return predictScoreOnCycleOneBillion(scores, cycle);
  }

  private int predictScoreOnCycleOneBillion(int[] scores, Cycle cycle) {
    final var targetCycle = 1_000_000_000;
    final var loopOffset = ((targetCycle - cycle.offset) % cycle.length) - 1;
    return scores[cycle.offset + loopOffset];
  }

  private int part1Score(Input input) {
    return score(rollNorth(parse(input.linesArray())));
  }

  private int countBetween(final char[] line, final char target, final int nearestRock, final int offset) {
    var count = 0;
    for (int i = max(nearestRock, 0); i < offset; i++) {
      if (line[i] == target) {
        count++;
      }
    }
    return count;
  }

  private int closestRock(final char[] line, final int offset) {
    for (int i = offset; i >= 0; i--) {
      if (line[i] == '#') {
        return i;
      }
    }
    return -1;
  }

  private Cycle findCycle(int[] scores) {
    int tortoise = 0;
    int hare = 1;

    int length;
    int minLength = 4;

    do {
      while (scores[tortoise] != scores[hare]) {
        tortoise++;
        hare += 2;
      }

      int pt1 = tortoise;
      int pt2 = hare;
      length = 0;
      while (scores[pt1] == scores[pt2] && pt1 < hare) {
        pt1++;
        pt2++;
        length++;
      }

      if (length < minLength) {
        // try again...
        tortoise++;
        hare += 2;
      }
    } while(length < minLength);

    return new Cycle(tortoise, length);
  }

  private int[] scoreCycle(Input input, int cycles) {
    char[][] matrix = parse(input.linesArray());
    int[] scores = new int[cycles];

    for (int cycle = 0; cycle < cycles; cycle++) {
      matrix = rollEast(rollSouth(rollWest(rollNorth(matrix))));
      scores[cycle] = score(matrix);
    }
    return scores;
  }

  private int score(char[][] matrix) {
    var lines = rotateCW(rotateCW(rotateCW(matrix)));
    var score = 0;
    for (final char[] line : lines) {
      for (int i = 0; i < line.length; i++) {
        if (line[i] == 'O') {
          score += (line.length - i);
        }
      }
    }
    return score;
  }

  private char[][] rollNorth(final char[][] matrix) {
    var temp = rotateCW(rotateCW(rotateCW(matrix)));
    return rotateCW(rollWest(temp));
  }

  private char[][] rollWest(final char[][] matrix2) {
    final var copyOfMatrix = new char[matrix2.length][matrix2[0].length];
    for (int i = 0; i < matrix2.length; i++) {
      copyOfMatrix[i] = Arrays.copyOf(matrix2[i], matrix2[i].length);
    }

    for (int x = 0; x < copyOfMatrix.length; x++) {
      for (int y = 0; y < copyOfMatrix[x].length; y++) {
        if (copyOfMatrix[x][y] == 'O') {
          var nearestRock = closestRock(copyOfMatrix[x], y);
          var between = countBetween(copyOfMatrix[x], 'O', nearestRock, y);

          copyOfMatrix[x][y] = '.';
          copyOfMatrix[x][nearestRock + 1 + between] = 'O';
        }
      }
    }
    return copyOfMatrix;
  }

  private char[][] rollSouth(final char[][] matrix) {
    var temp = rollWest(rotateCW(matrix));
    return rotateCW(rotateCW(rotateCW(temp)));
  }

  private char[][] rollEast(final char[][] matrix) {
    var temp = rotateCW(rotateCW(matrix));
    return rotateCW(rotateCW(rollWest(temp)));
  }

  public char[][] parse(String[] input) {
    char[][] result = new char[input.length][input[0].length()];

    for (int x = 0; x < input.length; x++) {
      result[x] = input[x].toCharArray();
    }

    return result;
  }

  private char[][] rotateCW(char[][] matrix) {
    var result = new char[matrix[0].length][matrix.length];
    for (int x = 0; x < matrix.length; x++) {
      for (int y = 0; y < matrix[0].length; y++) {
        result[y][result[0].length - x - 1] = matrix[x][y];
      }
    }
    return result;
  }
}
