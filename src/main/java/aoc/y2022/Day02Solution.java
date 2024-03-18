package aoc.y2022;


import org.junit.jupiter.api.Test;

import utils.AdventOfCode;
import utils.Input;

import static aoc.y2022.Day02Solution.State.Paper;
import static aoc.y2022.Day02Solution.State.Rock;
import static aoc.y2022.Day02Solution.State.Scissors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;

@AdventOfCode(year = 2022, day = 2, name = "Rock Paper Scissors")
public class Day02Solution {

  enum State {
    Rock, Paper, Scissors;

    public State beatenBy() {
      return switch (this) {
        case Rock -> Paper;
        case Paper -> Scissors;
        case Scissors -> Rock;
      };
    }

    public State willBeat() {
      return switch (this) {
        case Rock -> Scissors;
        case Paper -> Rock;
        case Scissors -> Paper;
      };
    }
  }

  @Test
  public void part2() {
    assertEquals(14979, calculateScore(input(this)));
  }

  public int calculateScore(final Input input) {
    int score = 0;

    for (final String line : input.lines()) {
      final String[] parts = line.trim().split("\\s+");

      final State opponent = opponent(parts[0]);
//      final State us = us(parts[1]);
      final State us = predict(parts[1], opponent);

      if (us == opponent) {
        score += 3; // draw
      }
      if (us.willBeat() == opponent) {
        score += 6; // win
      }

      score += us.ordinal() + 1;
    }

    return score;
  }

  private static State opponent(final String s) {
    return switch (s) {
      case "A" -> Rock;
      case "B" -> Paper;
      case "C" -> Scissors;
      default -> throw new IllegalStateException();
    };
  }

  private static State us(final String s) {
    return switch (s) {
      case "X" -> Rock;
      case "Y" -> Paper;
      case "Z" -> Scissors;
      default -> throw new IllegalStateException();
    };
  }

  private static State predict(final String s, final State opponent) {
    return switch (s) {
      case "X" -> opponent.willBeat();
      case "Y" -> opponent;
      case "Z" -> opponent.beatenBy();
      default -> throw new IllegalStateException();
    };
  }

}
