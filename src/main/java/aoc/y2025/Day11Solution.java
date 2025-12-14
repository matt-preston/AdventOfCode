package aoc.y2025;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.LinkedList;
import java.util.Map;

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

    @Test
    public void part2() {
        assertEquals(509312913844956L, solve2(input(this)));
    }

    private long solve(Input input) {
        var graph = graph(input);

        var cameFrom = bfs(graph, "you");

        return countPaths(cameFrom, "out", "you");
    }

    private long solve2(Input input) {
        var graph = graph(input);

        var cameFromSvr = bfs(graph, "svr");
        var cameFromDac = bfs(graph, "dac");
        var cameFromFft = bfs(graph, "fft");

        var r1 = countPaths(cameFromSvr, "dac", "svr") *
                 countPaths(cameFromDac, "fft", "dac") *
                 countPaths(cameFromFft, "out", "fft");

        var r2 = countPaths(cameFromSvr, "fft", "svr") *
                 countPaths(cameFromFft, "dac", "fft") *
                 countPaths(cameFromDac, "out", "dac");

        return r1 + r2;
    }

    private final Map<String, Long> cache = Maps.newHashMap();

    private long countPaths(HashMultimap<String, String> cameFrom, String from, String target) {
        var key = from + "->" + target;
        if (cache.containsKey(key)) {
            return cache.get(key);
        } else {
            var value = countPathsImpl(cameFrom, from, target);
            cache.put(key, value);
            return value;
        }
    }

    private long countPathsImpl(HashMultimap<String, String> cameFrom, String from, String target) {
        var count = 0L;
        for (String to : cameFrom.get(from)) {
            if (to.equals(target)) {
                count++;
            } else {
                count += countPaths(cameFrom, to, target);
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
