package aoc.y2022;

import org.junit.jupiter.api.Test;
import utils.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2022, day = 8, name = "Treetop Tree House")
public class Day08Solution {

    private static final String MOCK = """
            30373
            25512
            65332
            33549
            35390
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(21, visible(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(1798, visible(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(8, scenicScore(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(259308, scenicScore(input(this)));
    }

    private int visible(Input input) {
        char[][] matrix = Utils.matrix(input.text());
        var result = 0;

        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                var position = new Vector2(x, y);
                var height = matrix[y][x];

                if (visible(matrix, height, position, Direction.NORTH)
                        || visible(matrix, height, position, Direction.SOUTH)
                        || visible(matrix, height, position, Direction.EAST)
                        || visible(matrix, height, position, Direction.WEST)) {
                    result++;
                }
            }
        }
        return result;
    }

    private int scenicScore(Input input) {
        char[][] matrix = Utils.matrix(input.text());
        var max = 0;

        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                var position = new Vector2(x, y);
                var height = matrix[y][x];

                var scenicScore = viewingDistance(matrix, height, position, Direction.NORTH)
                        * viewingDistance(matrix, height, position, Direction.SOUTH)
                        * viewingDistance(matrix, height, position, Direction.EAST)
                        * viewingDistance(matrix, height, position, Direction.WEST);

                max = Integer.max(max, scenicScore);
            }
        }
        return max;
    }

    private boolean visible(char[][] matrix, char height, Vector2 position, Direction direction) {
        var next = position.move(direction);
        if (Utils.contains(matrix, next)) {
            if (Utils.get(matrix, next) < height) {
                return visible(matrix, height, next, direction);
            } else {
                return false;
            }
        }

        return true;
    }

    private int viewingDistance(char[][] matrix, char height, Vector2 position, Direction direction) {
        var next = position.move(direction);
        if (Utils.contains(matrix, next)) {
            if (Utils.get(matrix, next) < height) {
                return 1 + viewingDistance(matrix, height, next, direction);
            } else {
                return 1;
            }
        }

        return 0;
    }
}
