package aoc.y2024;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;
import utils.Vector2;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 20, name = "Race Condition")
public class Day20Solution {

    private static final String MOCK = """
            ###############
            #...#...#.....#
            #.#.#.#.#.###.#
            #S#...#.#.#...#
            #######.#.#.###
            #######.#.#...#
            #######.#.###.#
            ###..E#...#...#
            ###.#######.###
            #...###...#...#
            #.#####.#.###.#
            #.#...#.#.#...#
            #.#.#.#.#.#.###
            #...#...#...###
            ###############
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(4, solve(mockInput(MOCK), 2, 30));
    }

    @Test
    public void part1() {
        assertEquals(1459, solve(input(this), 2, 100));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(285, solve(mockInput(MOCK), 20, 50));
    }

    @Test
    public void part2() {
        assertEquals(1016066, solve(input(this), 20, 100));
    }

    // start and end are both on the main path
    record Cheat(Vector2 start, Vector2 end, int length) {
    }

    private int solve(Input input, int maxCheatLength, int saveAtLeast) {
        var map = Utils.matrix(input.text());
        var path = path(map);

        var indices = Maps.<Vector2, Integer>newHashMap();
        for (int i = 0; i < path.size(); i++) {
            indices.put(path.get(i), i);
        }

        var count = 0;

        for (Vector2 position : path) {
            for (Cheat cheat : cheats(map, position, maxCheatLength)) {
                var endIndex = indices.get(cheat.end());
                var startIndex = indices.get(cheat.start());

                int diff = endIndex - (startIndex + cheat.length());
                if (diff >= saveAtLeast) {
                    count++;
                }
            }
        }

        return count;
    }

    record State(Vector2 position, int cheatLength) {
    }

    private Collection<Cheat> cheats(char[][] map, Vector2 start, int maxCheatLength) {
        var frontier = Lists.<State>newLinkedList();
        frontier.add(new State(start, 0));

        var seen = Sets.<Vector2>newHashSet();
        seen.add(start);

        var result = Lists.<Cheat>newArrayList();

        while (!frontier.isEmpty()) {
            var current = frontier.poll();

            for (Vector2 next : new Vector2[]{current.position().north(), current.position().south(), current.position().east(), current.position().west()}) {
                if (next.x() > 0 && next.x() < map[0].length - 1 && next.y() > 0 && next.y() < map.length - 1) {
                    if(seen.add(next)) {
                        int len = current.cheatLength() + 1;

                        if (len > 1 && Utils.get(map, next) != '#') {
                            result.add(new Cheat(start, next, len));
                        }

                        if (len < maxCheatLength) {
                            frontier.add(new State(next, len));
                        }
                    }
                }
            }
        }

        return result;
    }

    private List<Vector2> path(char[][] map) {
        var start = Utils.find(map, 'S');
        var target = Utils.find(map, 'E');

        var frontier = Lists.<Vector2>newLinkedList();
        frontier.add(start);

        var parents = Maps.<Vector2, Vector2>newHashMap();

        while (!frontier.isEmpty()) {
            var current = frontier.poll();
            for (var next : new Vector2[]{current.north(), current.south(), current.east(), current.west()}) {
                if (Utils.contains(map, next) && Utils.get(map, next) != '#') {
                    if (!parents.containsKey(next)) {
                        frontier.add(next);
                        parents.put(next, current);
                    }
                }
            }
        }

        var node = target;
        var path = Lists.<Vector2>newArrayList();
        path.add(target);
        while ((node = parents.get(node)) != start) {
            path.add(node);
        }
        path.add(start);

        return path.reversed();
    }
}
