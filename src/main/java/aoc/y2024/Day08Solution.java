package aoc.y2024;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;
import utils.Vector2;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;

import static com.google.common.collect.Sets.combinations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;
import static utils.Utils.contains;

@AdventOfCode(year = 2024, day = 8, name = "Resonant Collinearity")
public class Day08Solution {

    private static final String MOCK = """
            ............
            ........0...
            .....0......
            .......0....
            ....0.......
            ......A.....
            ............
            ............
            ........A...
            .........A..
            ............
            ............
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(14, antinodes(mockInput(MOCK), pt1()));
    }

    @Test
    public void part1() {
        assertEquals(367, antinodes(input(this), pt1()));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(34, antinodes(mockInput(MOCK), pt2()));
    }

    @Test
    public void part2() {
        assertEquals(1285, antinodes(input(this), pt2()));
    }

    interface AntinodeCollector {
        void collect(Vector2 v1, Vector2 v2, BinaryOperator<Vector2> op, HashSet<Vector2> set, char[][] matrix);
    }

    private AntinodeCollector pt1() {
        return (v1, v2, op, set, matrix) -> {
            var antinode = op.apply(v1, v2);
            if (contains(matrix, antinode)) {
                set.add(antinode);
            }
        };
    }

    private AntinodeCollector pt2() {
        return (v1, v2, op, set, matrix) -> {
            var antinode = v1;
            do {
                set.add(antinode);
                antinode = op.apply(antinode, v2);
            } while (contains(matrix, antinode));
        };
    }

    private int antinodes(Input input, AntinodeCollector collector) {
        var matrix = Utils.matrix(input);

        var map = HashMultimap.<Character, Vector2>create();
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[0].length; x++) {
                if (matrix[y][x] != '.') {
                    map.put(matrix[y][x], new Vector2(x, y));
                }
            }
        }

        var set = Sets.<Vector2>newHashSet();

        for (Character type : map.keySet()) {
            for (Set<Vector2> pair : combinations(map.get(type), 2)) {
                var iter = pair.iterator();
                var antenna1 = iter.next();
                var antenna2 = iter.next();

                var slope = antenna1.subtract(antenna2);

                collector.collect(antenna1, slope, Vector2::add, set, matrix);
                collector.collect(antenna2, slope, Vector2::subtract, set, matrix);
            }
        }

        return set.size();
    }
}
