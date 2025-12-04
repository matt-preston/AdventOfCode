package aoc.y2025;

import utils.AdventOfCode;
import org.junit.jupiter.api.Test;
import utils.Input;
import utils.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2025, day = 4, name = "Printing Department")
public class Day04Solution {

    private static final String MOCK = """
            ..@@.@@@@.
            @@@.@.@.@@
            @@@@@.@.@@
            @.@@@@..@.
            @@.@@@@.@@
            .@@@@@@@.@
            .@.@.@.@@@
            @.@@@.@@@@
            .@@@@@@@@.
            @.@.@@@.@.
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(13, solve(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(1464, solve(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(43, solvePt2(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(8409, solvePt2(input(this)));
    }

    private int solve(Input input) {
        var map = Utils.matrix(input);
        return removePaperRolls(map);
    }

    private int solvePt2(Input input) {
        var map = Utils.matrix(input);
        var total = 0;

        while(true) {
            var removed = removePaperRolls(map);
            total += removed;
            
            if (removed == 0) {
                break;
            }
        }

        return total;
    }

    private int removePaperRolls(char[][] map) {
        var copy = Utils.copy(map);
        var removed = 0;

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                var rollsPaper = neighbour(copy, x, y, 1, 0) +
                        neighbour(copy, x, y, 1, 1) +
                        neighbour(copy, x, y, 0,  1) +
                        neighbour(copy, x, y, -1, 1) +
                        neighbour(copy, x, y, -1, 0) +
                        neighbour(copy, x, y, -1, -1) +
                        neighbour(copy, x, y, 0, -1) +
                        neighbour(copy, x, y, 1, -1);

                if (copy[y][x] == '@' && rollsPaper < 4) {
                    map[y][x] = '.';
                    removed++;
                }
            }
        }

        return removed;
    }

    private int neighbour(char[][] map, int sx, int sy, int dx, int dy) {
        var x = sx + dx;
        var y = sy +dy;

        if (y >= 0 && y < map.length) {
            if (x >= 0 && x < map[0].length) {
                if (map[y][x] == '@') {
                    return 1;
                }
            }
        }
        return 0;
    }
}
