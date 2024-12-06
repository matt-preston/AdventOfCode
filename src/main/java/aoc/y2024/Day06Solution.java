package aoc.y2024;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;
import utils.Vector2;

import java.util.Set;

import static aoc.y2024.Day06Solution.Direction.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 6, name = "Guard Gallivant")
public class Day06Solution {

    private static final String MOCK = """
            ....#.....
            .........#
            ..........
            ..#.......
            .......#..
            ..........
            .#..^.....
            ........#.
            #.........
            ......#...
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(41, path(mockInput(MOCK)).size());
    }

    @Test
    public void part1() {
        assertEquals(4890, path(input(this)).size());
    }

    @Test
    public void part2WithMockData() {
        assertEquals(6, countLoops(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(1995, countLoops(input(this)));
    }

    enum Direction { UP, RIGHT, DOWN, LEFT}

    private Set<Vector2> path(Input input) {
        var matrix = Utils.matrix(input.text());
        var position = findStart(matrix);
        var dir = UP;
        var seen = Sets.newHashSet(position);

        while(true) {
            var next = walk(position, dir);
            if (next.x() < 0 || next.x() > matrix[0].length - 1 || next.y() < 0 || next.y() > matrix.length - 1) {
                break; // found an exit
            } else if (matrix[next.y()][next.x()] == '#') {
                dir = turnRight(dir);
            } else {
                seen.add(next);
                position = next;
            }
        }

        return seen;
    }

    private int countLoops(Input input) {
        var matrix = Utils.matrix(input);
        var start = findStart(matrix);
        var count = 0;

        for (Vector2 position : path(input)) {
            // don't put an obstacle at the starting point
            if (matrix[position.y()][position.x()] != '^') {
                matrix[position.y()][position.x()] = '#';
                if (loop(matrix, start)) {
                    count++;
                }
                matrix[position.y()][position.x()] = '.';
            }
        }

        return count;
    }

    private boolean loop(char[][] matrix, Vector2 start) {
        var position = start;
        var dir = UP;
        var seen = HashMultimap.create();
        seen.put(position, dir);

        while(true) {
            var next = walk(position, dir);
            if (next.x() < 0 || next.x() > matrix[0].length - 1 || next.y() < 0 || next.y() > matrix.length - 1) {
                return false;
            } else if (matrix[next.y()][next.x()] == '#') {
                dir = turnRight(dir);
            } else if (seen.containsEntry(next, dir)) {
                return true;
            } else {
                seen.put(next, dir);
                position = next;
            }
        }
    }

    private Direction turnRight(Direction dir) {
        return switch (dir) {
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
            case LEFT -> UP;
        };
    }

    private Vector2 walk(Vector2 position, Direction d) {
        return switch (d) {
            case UP -> new Vector2(position.x(), position.y() - 1);
            case DOWN -> new Vector2(position.x(), position.y() + 1);
            case RIGHT -> new Vector2(position.x() + 1, position.y());
            case LEFT -> new Vector2(position.x() - 1, position.y());
        };
    }

    private Vector2 findStart(char[][] matrix) {
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                if (matrix[y][x] == '^') {
                    return new Vector2(x, y);
                }
            }
        }
        throw new IllegalStateException();
    }
}
