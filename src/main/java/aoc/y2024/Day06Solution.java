package aoc.y2024;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;
import utils.Vector2;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Direction.NORTH;
import static utils.Input.input;
import static utils.Input.mockInput;
import static utils.Utils.*;

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
        var position = checkNotNull(find(matrix, '^'));
        var dir = NORTH;
        var seen = Sets.newHashSet(position);

        while (true) {
            var next = position.move(dir);
            if (!contains(matrix, next)) {
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
        var dir = NORTH;
        var seen = HashMultimap.create();
        seen.put(position, dir);

        while (true) {
            var next = position.move(dir);
            if (!Utils.contains(matrix, next)) {
                return false;
            } else if (Utils.get(matrix, next) == '#') {
                dir = dir.turnRight();
            } else if (seen.containsEntry(next, dir)) {
                return true;
            } else {
                seen.put(next, dir);
                position = next;
            }
        }
    }
}
