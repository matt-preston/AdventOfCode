package aoc.y2024;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Direction;
import utils.Input;
import utils.Vector2;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Direction.UP;
import static utils.Input.input;
import static utils.Input.mockInput;
import static utils.Utils.find;
import static utils.Utils.matrix;

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

    private Set<Vector2> path(Input input) {
        var matrix = matrix(input.text());
        var position = find(matrix, '^');
        var dir = UP;
        var seen = Sets.newHashSet(position);

        while(true) {
            var next = walk(position, dir);
            if (next.x() < 0 || next.x() > matrix[0].length - 1 || next.y() < 0 || next.y() > matrix.length - 1) {
                break; // found an exit
            } else if (matrix[next.y()][next.x()] == '#') {
                dir = dir.turnRight();
            } else {
                seen.add(next);
                position = next;
            }
        }

        return seen;
    }

    private int countLoops(Input input) {
        var matrix = matrix(input);
        var start = find(matrix, '^');
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
                dir = dir.turnRight();
            } else if (seen.containsEntry(next, dir)) {
                return true;
            } else {
                seen.put(next, dir);
                position = next;
            }
        }
    }

    private Vector2 walk(Vector2 position, Direction d) {
        return switch (d) {
            case UP -> new Vector2(position.x(), position.y() - 1);
            case DOWN -> new Vector2(position.x(), position.y() + 1);
            case RIGHT -> new Vector2(position.x() + 1, position.y());
            case LEFT -> new Vector2(position.x() - 1, position.y());
        };
    }
}
