package aoc.y2023;

import java.util.List;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

import org.junit.jupiter.api.Test;

import utils.AdventOfCode;
import utils.Input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;


@AdventOfCode(year = 2023, day = 2)
public class Day02Test {

  record Game(List<Sample> samples) {

    public Game() {
      this(Lists.newArrayList());
    }

    public boolean possible(int maxRed, int maxGreen, int maxBlue) {
      for (final Sample sample : samples) {
        if (sample.red > maxRed || sample.green > maxGreen || sample.blue > maxBlue) {
          return false;
        }
      }

      return true;
    }

    public Sample minimumPossible() {
      return new Sample(
          samples.stream().map(Sample::red).mapToInt(i -> i).max().orElse(0),
          samples.stream().map(Sample::green).mapToInt(i -> i).max().orElse(0),
          samples.stream().map(Sample::blue).mapToInt(i -> i).max().orElse(0)
      );
    }
  }

  record Sample(int red, int green, int blue) {

    public int power() {
      return red * green * blue;
    }
  }

  @Test
  public void part1() {
    int possibleGames = 0;
    int index = 1;
    for (final Game game : games(input(this))) {
      if (game.possible(12, 13, 14)) {
        possibleGames += index;
      }
      index++;
    }

    assertEquals(2449, possibleGames);
  }

  @Test
  public void part2() {
    int sum = 0;
    for (final Game game : games(input(this))) {
      sum += game.minimumPossible().power();
    }

    assertEquals(63981, sum);
  }

  private List<Game> games(final Input input) {
    var games = Lists.<Game>newArrayList();
    for (final String line : input.lines()) {
      var game = new Game();
      for (final String sample : line.split(":", 2)[1].split(";")) {
        game.samples.add(new Sample(count("red", sample), count("green", sample), count("blue", sample)));
      }
      games.add(game);
    }
    return games;
  }

  private int count(final String colour, final String sample) {
    final var matcher = Pattern.compile("(\\d+) " + colour).matcher(sample);
    return matcher.find() ?
        Integer.parseInt(matcher.group(1)) :
        0;
  }

}
