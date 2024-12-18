package aoc.y2024;

import com.google.common.collect.*;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;
import utils.Vector2;

import javax.crypto.spec.PSource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 18, name = "RAM Run")
public class Day18Solution {

    private static final String MOCK = """
            5,4
            4,2
            4,5
            3,0
            2,1
            6,3
            2,4
            1,5
            0,6
            3,3
            2,6
            5,1
            1,2
            5,5
            2,5
            6,5
            1,4
            0,4
            6,4
            1,1
            6,1
            1,0
            0,5
            1,6
            2,0
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(22, shortestPath(mockInput(MOCK), 7, 7, 12));
    }

    @Test
    public void part1() {
        assertEquals(340, shortestPath(input(this), 71, 71, 1024));
    }

    @Test
    public void part2WithMockData() {
        var input = mockInput(MOCK);
        assertEquals("6,1", binarySearchFirstFailing(input, 7, 7, 0, input.lines().size()));
    }

    @Test
    public void part2() {
        var input = input(this);
        assertEquals("34,32", binarySearchFirstFailing(input, 71, 71, 0, input.lines().size()));
    }

    public int shortestPath(Input input, int width, int height, int numberOfCorruptions) {
        var map = new char[height][width];
        for (char[] chars : map) {
            Arrays.fill(chars, '.');
        }

        for (String line : Iterables.limit(input.lines(), numberOfCorruptions)) {
            var ints = Utils.parseInts(line);
            map[ints.get(1)][ints.get(0)] = '#';
        }

        var start = new Vector2(0,0);
        var target = new Vector2(map[0].length - 1, map.length - 1);

        var frontier = Lists.<Vector2>newLinkedList();
        var distances = Maps.<Vector2, Integer>newHashMap();

        frontier.add(start);
        distances.put(start, 0);

        while(!frontier.isEmpty()) {
            var current = frontier.poll();

            for(var next : List.of(current.north(), current.east(), current.south(), current.west())) {
                if (Utils.contains(map, next) && Utils.get(map, next) != '#') {
                    if (!distances.containsKey(next)) {
                        distances.put(next, distances.get(current) + 1);
                        frontier.add(next);
                    }
                }
            }
        }

        return distances.getOrDefault(target, Integer.MAX_VALUE);
    }

    private String binarySearchFirstFailing(Input input, int width, int height, int from, int to) {
        var mid = from + ((to - from) / 2);
        if (shortestPath(input, width, height, mid) < Integer.MAX_VALUE) {
            // found a path, check mid + 1
            if (shortestPath(input, width, height, mid + 1) < Integer.MAX_VALUE) {
                // found another path, check to the right hand side
                return binarySearchFirstFailing(input, width, height, mid, to);
            } else {
                // Found the first corruption that blocks all paths
                return input.lines().get(mid);
            }
        } else {
            // No path, check to the left
            return binarySearchFirstFailing(input, width, height, from, mid);
        }
    }
}
