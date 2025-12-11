package aoc.y2025;

import aoc.y2023.Day19Solution;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import utils.AdventOfCode;
import org.junit.jupiter.api.Test;
import utils.Input;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2025, day = 11, name = "Reactor")
public class Day11Solution {

    private static final String MOCK = """
            aaa: you hhh
            you: bbb ccc
            bbb: ddd eee
            ccc: ddd eee fff
            ddd: ggg
            eee: out
            fff: out
            ggg: out
            hhh: ccc fff iii
            iii: out
            """;

    private static final String MOCK2 = """
            svr: aaa bbb
            aaa: fft
            fft: ccc
            bbb: tty
            tty: ccc
            ccc: ddd eee
            ddd: hub
            hub: fff
            eee: dac
            dac: fff
            fff: ggg hhh
            ggg: out
            hhh: out
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(5, solve(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(636, solve(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(2, solve2(mockInput(MOCK2)));
    }

    // 763_969_370_767_434 too high
    @Test
    public void part2() {
        assertEquals(0L, solve2(input(this)));
    }

    private int solve(Input input) {
        var graph = graph(input);

        var cameFrom = bfs(graph, "you");

        return simpleCountPaths(cameFrom, "out", "you");
    }

    // 382_196_088_773_040_186 unique paths
    private long solve2(Input input) {
        var graph = graph(input);

        var cameFrom = bfs(graph, "svr");

        return countPaths(cameFrom, "out", "svr", Set.of("out"));
    }

    private final Map<Key, Long> cache = Maps.newHashMap();

    record Key(String from, String target, Set<String> path){}

    private long countPaths(HashMultimap<String, String> cameFrom, String from, String target, Set<String> path) {
//        var key = from + "->" + target + ":" + path;
        var key = new Key(from, target, path);
        if (cache.containsKey(key)) {
            return cache.get(key);
        } else {
            var value = countPathsImpl(cameFrom, from, target, path);
            cache.put(key, value);
            return value;
        }
    }

    private long countPathsImpl(HashMultimap<String, String> cameFrom, String from, String target, Set<String> path) {
        var count = 0L;
        for (String to : cameFrom.get(from)) {
//            if (to.equals("dac")) dac = true;
//            if (to.equals("fft")) fft = true;

            if (to.equals(target)) {
                if (path.contains("dac") && path.contains("fft")) {
                    count++;

                    if (count % 1000 == 0) {
                        System.out.println(count);
                    }
                }
            } else {
                // OOM
                var newPath = Sets.newLinkedHashSet(path);
                newPath.add(to);
                count += countPaths(cameFrom, to, target, newPath);
            }
        }
        return count;
    }

    private int simpleCountPaths(HashMultimap<String, String> cameFrom, String from, String target) {
        var count = 0;
        for (String to : cameFrom.get(from)) {
            if (to.equals(target)) {
                count++;
            } else {
                count += simpleCountPaths(cameFrom, to, target);
            }
        }
        return count;
    }

    private HashMultimap<String, String> bfs(HashMultimap<String, String> graph, String from) {
        var frontier = new LinkedList<String>();
        frontier.add(from);

        var cameFrom = HashMultimap.<String, String>create();
        cameFrom.put(from, null);

        while (!frontier.isEmpty()) {
            var current = frontier.poll();

            for (String next : graph.get(current)) {
                if (!cameFrom.containsEntry(next, current)) {
                    frontier.add(next);
                    cameFrom.put(next, current);
                }
            }
        }

        return cameFrom;
    }

    private HashMultimap<String, String> graph(Input input) {
        var graph = HashMultimap.<String, String>create();
        for (String line : input.lines()) {
            var from = line.split(":");
            var to = from[1].trim().split(" ");
            for (String s : to) {
                graph.put(from[0], s);
            }
        }
        return graph;
    }


}
