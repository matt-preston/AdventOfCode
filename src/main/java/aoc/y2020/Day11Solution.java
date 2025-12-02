package aoc.y2020;

import utils.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2020, day = 11, name = "Seating System")
public class Day11Solution {

    private static final String MOCK = """
            L.LL.LL.LL
            LLLLLLL.LL
            L.L.L..L..
            LLLL.LL.LL
            L.LL.LL.LL
            L.LLLLL.LL
            ..L.L.....
            LLLLLLLLLL
            L.LLLLLL.L
            L.LLLLL.LL
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(37, solve(mockInput(MOCK), false));
    }

    @Test
    public void part1() {
        assertEquals(2183, solve(input(this), false));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(26, solve(mockInput(MOCK), true));
    }

    @Test
    public void part2() {
        assertEquals(1990, solve(input(this), true));
    }

    private int solve(Input input, boolean part2) {
        var matrix = Utils.matrix(input);

        var modified = false;
        do {
            modified = false;

            // Whoa - copy the matrix!
            var copy = new char[matrix.length][matrix[0].length];
            for (int i = 0; i < matrix.length; i++) {
                copy[i] = Arrays.copyOf(matrix[i], matrix[i].length);
            }

            for (int y = 0; y < matrix.length; y++) {
                for (int x = 0; x < matrix[0].length; x++) {
                    var occupied = occupied(matrix, x, y, 1, 0, part2) +
                            occupied(matrix, x, y, 1, 1, part2) +
                            occupied(matrix, x, y, 0,  1, part2) +
                            occupied(matrix, x, y, -1, 1, part2) +
                            occupied(matrix, x, y, -1, 0, part2) +
                            occupied(matrix, x, y, -1, -1, part2) +
                            occupied(matrix, x, y, 0, -1, part2) +
                            occupied(matrix, x, y, 1, -1, part2);

                    if (matrix[y][x] == 'L' && occupied == 0) {
                        copy[y][x] = '#';
                        modified = true;
                    }

                    if (matrix[y][x] == '#' && occupied >= (part2 ? 5 : 4)) {
                        copy[y][x] = 'L';
                        modified = true;
                    }
                }
            }

            matrix = copy;
        } while (modified);

        var count = 0;
        for (char[] chars : matrix) {
            for (char c : chars) {
                if (c == '#') {
                    count++;
                }
            }
        }

        return count;
    }

    private int occupied(char[][] map, int sx, int sy, int dx, int dy, boolean part2) {
        var x = sx + dx;
        var y = sy +dy;

        if (y >= 0 && y < map.length) {
            if (x >= 0 && x < map[0].length) {
                if (map[y][x] == '#') {
                    return 1;
                } else if (map[y][x] == 'L') {
                    return 0;
                } else if (part2) {
                    return occupied(map, x, y, dx, dy, part2);
                }
            }
        }
        return 0;
    }
}
