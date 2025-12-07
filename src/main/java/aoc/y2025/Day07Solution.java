package aoc.y2025;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2025, day = 7, name = "Laboratories")
public class Day07Solution {

    private static final String MOCK = """
            .......S.......
            ...............
            .......^.......
            ...............
            ......^.^......
            ...............
            .....^.^.^.....
            ...............
            ....^.^...^....
            ...............
            ...^.^...^.^...
            ...............
            ..^...^.....^..
            ...............
            .^.^.^.^.^...^.
            ...............
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(21, solve(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(1590, solve(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(40, solvePt2(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(20571740188555L, solvePt2(input(this)));
    }

    private long solve(Input input) {
        var map = Utils.matrix(input);

        var startY = 0;
        var startX = new String(map[0]).indexOf('S');

        return numSplittersVisited(map, startX, startY + 1, Sets.newHashSet());
    }

    private long solvePt2(Input input) {
        var map = Utils.matrix(input);

        var startY = 0;
        var startX = new String(map[0]).indexOf('S');

        return numSplittersVisited2(map, startX, startY + 1) + 1;
    }

    private long numSplittersVisited(char[][] map, int x, int y, Set<String> visited) {
        if (y >= map.length || x < 0 || x >= map[0].length || !visited.add(x + "," + y)) {
            return 0;
        }

        if (map[y][x] == '^') {
            return 1 + numSplittersVisited(map, x - 1, y, visited) + numSplittersVisited(map, x + 1, y, visited);
        } else {
            return numSplittersVisited(map, x, y + 1, visited);
        }
    }

    private final Map<String, Long> cache = Maps.newHashMap();

    private long numSplittersVisited2(char[][] map, int x, int y) {
        var key = x + "," + y;
        if (cache.containsKey(key)) {
            return cache.get(key);
        } else {
            var count = numSplittersVisited2Impl(map, x, y);
            cache.put(key, count);
            return count;
        }
    }

    private long numSplittersVisited2Impl(char[][] map, int x, int y) {
        if (y >= map.length || x < 0 || x >= map[0].length) {
            return 0;
        }

        if (map[y][x] == '^') {
            return 1 + numSplittersVisited2(map, x - 1, y) + numSplittersVisited2(map, x + 1, y);
        } else {
            return numSplittersVisited2(map, x, y + 1);
        }
    }
}
