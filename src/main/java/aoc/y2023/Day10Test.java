package aoc.y2023;

import java.awt.*;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.junit.jupiter.api.Test;

import utils.AdventOfCode;
import utils.Input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;


@AdventOfCode(year = 2023, day = 10)
public class Day10Test {

  record Coordinate(int x, int y) {

    public Coordinate west() {
      return new Coordinate(x, y - 1);
    }

    public Coordinate east() {
      return new Coordinate(x, y + 1);
    }

    public Coordinate north() {
      return new Coordinate(x - 1, y);
    }

    public Coordinate south() {
      return new Coordinate(x + 1, y);
    }

  }

  enum Pipe {
    V_PIPE('|'),
    H_PIPE('-'),
    L('L'),
    J('J'),
    _7('7'),
    F('F'),
    S('S');

    private final char c;

    Pipe(char c) {
      this.c = c;
    }

    public static Pipe get(char c) {
      for (final Pipe value : Pipe.values()) {
        if (value.c == c) {
          return value;
        }
      }
      return null;
    }

    public boolean connectsSouth() {
      return switch (this) {
        case V_PIPE -> true;
        case _7 -> true;
        case F -> true;
        case S -> true;
        default -> false;
      };
    }

    public boolean connectsNorth() {
      return switch (this) {
        case V_PIPE -> true;
        case L -> true;
        case J -> true;
        case S -> true;
        default -> false;
      };
    }

    public boolean connectsEast() {
      return switch (this) {
        case H_PIPE -> true;
        case L -> true;
        case F -> true;
        case S -> true;
        default -> false;
      };
    }

    public boolean connectsWest() {
      return switch (this) {
        case H_PIPE -> true;
        case J -> true;
        case _7 -> true;
        default -> false;
      };
    }
  }

  @Test
  public void part1WithMockData1() {
    assertEquals(4, furthestPointOnPath(mockInput("""
        -L|F7
        7S-7|
        L|7||
        -L-J|
        L|-JF
        """)));
  }

  @Test
  public void part1WithMockData2() {
    assertEquals(8, furthestPointOnPath(mockInput("""
        7-F7-
        .FJ|7
        SJLL7
        |F--J
        LJ.LJ
        """)));
  }

  @Test
  public void part1() {
    assertEquals(6649, furthestPointOnPath(input(this)));
  }

  @Test
  public void part2WithMockData1() {
    assertEquals(4, enclosedArea(mockInput("""
        ...........
        .S-------7.
        .|F-----7|.
        .||.....||.
        .||.....||.
        .|L-7.F-J|.
        .|..|.|..|.
        .L--J.L--J.
        ...........
        """)));
  }

  @Test
  public void part2WithMockData2() {
    assertEquals(8, enclosedArea(mockInput("""
        .F----7F7F7F7F-7....
        .|F--7||||||||FJ....
        .||.FJ||||||||L7....
        FJL7L7LJLJ||LJ.L-7..
        L--J.L7...LJS7F-7L7.
        ....F-J..F7FJ|L7L7L7
        ....L7.F7||L7|.L7L7|
        .....|FJLJ|FJ|F7|.LJ
        ....FJL-7.||.||||...
        ....L---J.LJ.LJLJ...
        """)));
  }

  @Test
  public void part2WithMockData3() {
    assertEquals(10, enclosedArea(mockInput("""
        FF7FSF7F7F7F7F7F---7
        L|LJ||||||||||||F--J
        FL-7LJLJ||||||LJL-77
        F--JF--7||LJLJ7F7FJ-
        L---JF-JLJ.||-FJLJJ7
        |F|F-JF---7F7-L7L|7|
        |FFJF7L7F-JF7|JL---7
        7-L-JL7||F7|L7F-7F7|
        L.L7LFJ|||||FJL7||LJ
        L7JLJL-JLJLJL--JLJ.L
        """)));
  }

  @Test
  public void part2() {
    assertEquals(601, enclosedArea(input(this)));
  }

  private int furthestPointOnPath(Input input) {
    return path(map(input)).size() / 2;
  }

  private Map<Coordinate, Pipe> map(Input input) {
    System.out.println(input.text());

    final Map<Coordinate, Pipe> map = Maps.newHashMap();
    final var linesArray = input.linesArray();
    for (int x = 0; x < linesArray.length; x++) {
      var inner = linesArray[x].toCharArray();
      for (int y = 0; y < inner.length; y++) {
        char c = inner[y];
        var pipe = Pipe.get(c);
        if (pipe != null) {
          map.put(new Coordinate(x, y), pipe);
        }
      }
    }
    return map;
  }

  private Set<Coordinate> path(Map<Coordinate, Pipe> map) {
    var start = map.entrySet().stream()
        .filter(e -> e.getValue() == Pipe.S)
        .findFirst().orElseThrow()
        .getKey();
    return path(start, map);
  }

  private Set<Coordinate> path(Coordinate next, Map<Coordinate, Pipe> map) {
    Set<Coordinate> path = Sets.newLinkedHashSet();
    path.add(next);

    while (true) {
      final var north = next.north();
      final var south = next.south();
      final var east = next.east();
      final var west = next.west();

      final var currentPipe = map.get(next);

      if (currentPipe.connectsNorth() && EnumSet.of(Pipe.V_PIPE, Pipe._7, Pipe.F).contains(map.get(north)) && !path.contains(north)) {
        path.add(north);
        next = north;
      } else if (currentPipe.connectsSouth() && EnumSet.of(Pipe.V_PIPE, Pipe.L, Pipe.J).contains(map.get(south)) && !path.contains(south)) {
        path.add(south);
        next = south;
      } else if (currentPipe.connectsEast() && EnumSet.of(Pipe.H_PIPE, Pipe.J, Pipe._7).contains(map.get(east)) && !path.contains(east)) {
        path.add(east);
        next = east;
      } else if (currentPipe.connectsWest() && EnumSet.of(Pipe.H_PIPE, Pipe.L, Pipe.F).contains(map.get(west)) && !path.contains(west)) {
        path.add(west);
        next = west;
      } else {
        break;
      }
    }

    return path;
  }

  private int enclosedArea(Input input) {
    final var map = map(input);
    final var path = path(map);

    var sum = 0;

    var poly = new Polygon(
        path.stream().mapToInt(Coordinate::x).toArray(),
        path.stream().mapToInt(Coordinate::y).toArray(),
        path.size()
    );

    final var linesArray = input.linesArray();
    for (int x = 0; x < linesArray.length; x++) {
      var inner = linesArray[x].toCharArray();
      for (int y = 0; y < inner.length; y++) {
        var inside = !path.contains(new Coordinate(x, y)) && poly.contains(x, y);
        System.out.print(inside ? "I" : "·");
        if (inside) {
          sum++;
        }
      }
      System.out.println();
    }
    return sum;
  }
}


