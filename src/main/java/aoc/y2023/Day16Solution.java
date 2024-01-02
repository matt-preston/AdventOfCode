package aoc.y2023;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.Set;

import static aoc.y2023.Day16Solution.Vector.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;
import static utils.Utils.matrix;

@AdventOfCode(year = 2023, day = 16, name = "The Floor Will Be Lava")
public class Day16Solution {

    record Vector(int x, int y) {

        static final Vector UP = new Vector(0, -1);
        static final Vector DOWN = new Vector(0, 1);
        static final Vector RIGHT = new Vector(1, 0);
        static final Vector LEFT = new Vector(-1, 0);

        static Vector vector(int x, int y) {
            return new Vector(x, y);
        }

        public Vector add(Vector other) {
            return new Vector(x + other.x, y + other.y);
        }
    }

    record Step(Vector point, Vector vector) {

    }

    @Test
    public void part1WithMockData() {
        assertEquals(46, sumEnergisedTiles(mockInput("""
                .|...\\....
                |.-.\\.....
                .....|-...
                ........|.
                ..........
                .........\\
                ..../.\\\\..
                .-.-/..|..
                .|....-|.\\
                ..//.|....
                """)));
    }

    @Test
    public void part1() {
        assertEquals(7728, sumEnergisedTiles(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(51, mostEnergisedTiles(mockInput("""
                .|...\\....
                |.-.\\.....
                .....|-...
                ........|.
                ..........
                .........\\
                ..../.\\\\..
                .-.-/..|..
                .|....-|.\\
                ..//.|....
                """)));
    }

    @Test
    public void part2() {
        assertEquals(8061, mostEnergisedTiles(input(this)));
    }

    private int mostEnergisedTiles(final Input input) {
        final var grid = matrix(input.text());

        int max = 0;

        for (int x = 0; x < grid[0].length; x++) {
            max = Math.max(max, uniqueCellsVisited(grid, vector(x, 0), DOWN));
            max = Math.max(max, uniqueCellsVisited(grid, vector(x, grid.length - 1), UP));
        }

        for (int y = 0; y < grid.length; y++) {
            max = Math.max(max, uniqueCellsVisited(grid, vector(0, y), RIGHT));
            max = Math.max(max, uniqueCellsVisited(grid, vector(grid[0].length - 1, y), LEFT));
        }

        return max;
    }

    private int sumEnergisedTiles(final Input input) {
        final var grid = matrix(input.text());
        return uniqueCellsVisited(grid, vector(0, 0), RIGHT);
    }

    private int uniqueCellsVisited(final char[][] grid, final Vector point, final Vector vector) {
        final var paths = Sets.<Step>newHashSet();
        uniqueCellsVisited(grid, point, vector, paths);
        return (int) paths.stream().map(Step::point).distinct().count();
    }

    private void uniqueCellsVisited(final char[][] grid, final Vector point, final Vector vector, final Set<Step> path) {
        if (point.x < 0 || point.x >= grid[0].length || point.y < 0 || point.y >= grid.length) {
            return; // out of bounds
        }

        var next = new Step(point, vector);

        if (path.contains(next)) {
            return; // cycle
        }

        path.add(next);

        switch (grid[point.y][point.x]) {
            case '\\' -> {
                if (vector.x > 0) { // moving left to right
                    uniqueCellsVisited(grid, point.add(DOWN), DOWN, path);
                } else if (vector.x < 0) { // moving right to left
                    uniqueCellsVisited(grid, point.add(UP), UP, path);
                } else if (vector.y > 0) { // moving down
                    uniqueCellsVisited(grid, point.add(RIGHT), RIGHT, path);
                } else {
                    uniqueCellsVisited(grid, point.add(LEFT), LEFT, path);
                }
            }
            case '/' -> {
                if (vector.x > 0) { // moving left to right
                    uniqueCellsVisited(grid, point.add(UP), UP, path);
                } else if (vector.x < 0) { // moving right to left
                    uniqueCellsVisited(grid, point.add(DOWN), DOWN, path);
                } else if (vector.y > 0) { // moving down
                    uniqueCellsVisited(grid, point.add(LEFT), LEFT, path);
                } else {
                    uniqueCellsVisited(grid, point.add(RIGHT), RIGHT, path);
                }
            }
            case '-' -> {
                if (vector.x == 0) { // moving vertically
                    uniqueCellsVisited(grid, point.add(RIGHT), RIGHT, path);
                    uniqueCellsVisited(grid, point.add(LEFT), LEFT, path);
                } else {
                    uniqueCellsVisited(grid, point.add(vector), vector, path);
                }
            }
            case '|' -> {
                if (vector.y == 0) { // moving horizontally
                    uniqueCellsVisited(grid, point.add(UP), UP, path);
                    uniqueCellsVisited(grid, point.add(DOWN), DOWN, path);
                } else {
                    uniqueCellsVisited(grid, point.add(vector), vector, path);
                }
            }
            default -> uniqueCellsVisited(grid, point.add(vector), vector, path); // '.'
        }
    }
}
