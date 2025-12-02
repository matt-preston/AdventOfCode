package aoc.y2020;

import utils.AdventOfCode;
import org.junit.jupiter.api.Test;
import utils.Input;
import utils.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2020, day = 3, name = "Toboggan Trajectory")
public class Day03Solution {

    private static final String MOCK = """
            ..##.......
            #...#...#..
            .#....#..#.
            ..#.#...#.#
            .#...##..#.
            ..#.##.....
            .#.#.#....#
            .#........#
            #.##...#...
            #...##....#
            .#..#...#.#
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(7, part1(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(209, part1(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(336, part2(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(1574890240, part2(input(this)));
    }

    private int part1(Input input) {
        return solve(input, 3, 1);
    }

    private int part2(Input input) {
        return solve(input, 1, 1) *
                solve(input, 3, 1) *
                solve(input, 5, 1) *
                solve(input, 7, 1) *
                solve(input, 1, 2);
    }

    private int solve(Input input, int dx, int dy) {
        var matrix = Utils.matrix(input);

        var x = 0;
        var y = 0;

        var trees = 0;

        while(y < matrix.length - 1) {
            x += dx;
            y += dy;

            if (matrix[y][x % matrix[0].length] == '#') {
                trees++;
            }
        }

        return trees;
    }
}
