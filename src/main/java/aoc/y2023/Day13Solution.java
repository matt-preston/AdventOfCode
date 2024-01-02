package aoc.y2023;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.Arrays;

import static aoc.y2023.Day13Solution.Direction.HORIZONTAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;
import static utils.Utils.matrix;
import static utils.Utils.rotateCW;

@AdventOfCode(year = 2023, day = 13, name = "Point of Incidence")
public class Day13Solution {

    enum Direction {VERTICAL, HORIZONTAL}

    record Symmetry(Direction direction, int count) {

        static Symmetry NULL = new Symmetry(HORIZONTAL, -1);

        int score() {
            return switch (this.direction) {
                case HORIZONTAL -> count * 100;
                case VERTICAL -> count;
            };
        }

        int rowIndex() {
            return switch (this.direction) {
                case HORIZONTAL -> count - 1;
                case VERTICAL -> -1;
            };
        }

        int columnIndex() {
            return switch (this.direction) {
                case VERTICAL -> count - 1;
                case HORIZONTAL -> -1;
            };
        }
    }

    public static final String MOCK = """
            #.##..##.
            ..#.##.#.
            ##......#
            ##......#
            ..#.##.#.
            ..##..##.
            #.#.##.#.
                  
            #...##..#
            #....#..#
            ..##..###
            #####.##.
            #####.##.
            ..##..###
            #....#..#
            """;

    @Test
    public void part1MockData() {
        assertEquals(405, summarizeSymmetry(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(31877, summarizeSymmetry(input(this)));
    }

    @Test
    public void part2MockData() {
        assertEquals(400, summarizeSymmetryWithReplacements(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(42996, summarizeSymmetryWithReplacements(input(this)));
    }

    private int summarizeSymmetryWithReplacements(final Input input) {
        final var notes = Arrays.stream(input.text().split("\n\n")).toArray(String[]::new);

        var sum = 0;

        for (final String note : notes) {
            final var matrix = matrix(note);
            final var originalSymmetry = find(matrix);

            loop:
            {
                for (int x = 0; x < matrix.length; x++) {
                    for (int y = 0; y < matrix[0].length; y++) {
                        if (matrix[x][y] == '.') {
                            matrix[x][y] = '#';
                            final var newSymmetry = find(matrix, originalSymmetry);
                            if (newSymmetry != null) {
                                sum += newSymmetry.score();
                                System.out.println(newSymmetry);
                                break loop;
                            }
                            matrix[x][y] = '.'; // put it back
                        }
                    }
                }
            }
        }

        return sum;
    }

    private int summarizeSymmetry(Input input) {
        final var notes = Arrays.stream(input.text().split("\n\n")).toArray(String[]::new);

        var sum = 0;

        for (final String note : notes) {
            final var matrix = matrix(note);

            final var symmetry = find(matrix);
            sum += symmetry.score();
            System.out.println(symmetry);
        }

        return sum;
    }

    private Symmetry find(char[][] matrix) {
        return find(matrix, Symmetry.NULL);
    }

    private Symmetry find(char[][] matrix, Symmetry previousMatch) {
        final var rows = findHorizontalSymmetry(matrix, previousMatch.rowIndex());
        if (rows > -1) {
            return new Symmetry(HORIZONTAL, rows);
        }

        final var columns = findHorizontalSymmetry(rotateCW(matrix), previousMatch.columnIndex());
        if (columns > -1) {
            return new Symmetry(Direction.VERTICAL, columns);
        }

        return null;
    }

    private int findHorizontalSymmetry(char[][] lines, int ignoreRow) {
        for (int i = 0; i < lines.length - 1; i++) {
            if (i != ignoreRow && Arrays.equals(lines[i], lines[i + 1])) {
                int x = i;
                int y = i + 1;
                boolean match = true;
                while (x >= 0 && y < lines.length && match) {
                    match = Arrays.equals(lines[x], lines[y]);
                    x--;
                    y++;
                }

                if (match) {
                    return i + 1;
                }
            }
        }
        return -1;
    }
}
