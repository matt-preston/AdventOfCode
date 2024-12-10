package aoc.y2024;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;
import utils.Vector2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 10, name = "Hoof It")
public class Day10Solution {

    private static final String MOCK = """
            89010123
            78121874
            87430965
            96549874
            45678903
            32019012
            01329801
            10456732
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(36, sum(mockInput(MOCK), HashSet::new));
    }

    @Test
    public void part1() {
        assertEquals(472, sum(input(this), HashSet::new));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(81, sum(mockInput(MOCK), ArrayList::new));
    }

    @Test
    public void part2() {
        assertEquals(969, sum(input(this), ArrayList::new));
    }

    private int sum(Input input, Supplier<Collection<Vector2>> factory) {
        char[][] matrix = Utils.matrix(input);

        var sum = 0;
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[0].length; x++) {
                if (matrix[y][x] == '0') {
                    sum += paths(matrix, new Vector2(x, y), factory).size();
                }
            }
        }

        return sum;
    }

    private Collection<Vector2> paths(char[][] matrix, Vector2 start, Supplier<Collection<Vector2>> factory) {
        var frontier = Lists.<Vector2>newLinkedList();
        frontier.add(start);

        var result = factory.get();

        while (!frontier.isEmpty()) {
            final var current = frontier.poll();
            if (Utils.get(matrix, current) == '9') {
                result.add(current);
            } else {
                for (Vector2 movement : MOVEMENTS) {
                    var next = current.add(movement);
                    if (Utils.contains(matrix, next) && Utils.get(matrix, current) + 1 == Utils.get(matrix, next)) {
                        frontier.add(next);
                    }
                }
            }
        }

        return result;
    }

    private static final List<Vector2> MOVEMENTS = List.of(
            new Vector2(-1, 0), // left
            new Vector2(1, 0),  // right
            new Vector2(0, -1), // up
            new Vector2(0, 1)   // down
    );
}
