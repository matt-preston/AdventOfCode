package aoc.y2024;

import com.google.common.collect.*;
import com.google.common.primitives.Chars;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;
import utils.Vector2;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;
import static utils.Utils.*;

@AdventOfCode(year = 2024, day = 21, name = "Keypad Conundrum")
public class Day21Solution {

    private static final String MOCK = """
            029A
            980A
            179A
            456A
            379A
            """;

    private static final Multimap<Path, String> NUMERIC_MAP = pathMap("""
            789
            456
            123
             0A
            """);

    private static final Multimap<Path, String> DIRECTION_MAP = pathMap("""
             ^A
            <v>
            """);

    @Test
    public void part1WithMockData() {
        assertEquals(126384, complexity(mockInput(MOCK), 2));
    }

    @Test
    public void part1() {
        assertEquals(134120, complexity(input(this), 2));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(154115708116294L, complexity(mockInput(MOCK), 25));
    }

    @Test
    public void part2() {
        assertEquals(167389793580400L, complexity(input(this), 25));
    }

    private long complexity(Input input, int depth) {
        var total = 0L;
        for (String code : input.lines()) {
            var min = sequences(NUMERIC_MAP, code, 0, 'A', "").stream()
                    .mapToLong(seq -> shortestSequence(seq, depth))
                    .min()
                    .orElseThrow();

            var asNumber = Integer.parseInt(code.replaceAll("\\D", ""));
            total += asNumber * min;
        }

        return total;
    }

    private final Map<String, Long> cache = Maps.newHashMap();

    private long shortestSequence(String keys, int depth) {
        var cacheKey = keys + depth;
        if (!cache.containsKey(cacheKey)) {
            var result = shortestSequenceImpl(keys, depth);
            cache.put(cacheKey, result);
            return result;
        } else {
            return cache.get(cacheKey);
        }
    }

    private long shortestSequenceImpl(String keys, int depth) {
        if (depth == 0) {
            return keys.length();
        } else {
            var total = 0L;

            var from = 0;
            var to = 0;
            while ((to = keys.indexOf('A', from)) > -1) {
                var subKey = keys.substring(from, to + 1);

                total += sequences(DIRECTION_MAP, subKey, 0, 'A', "").stream()
                        .mapToLong(seq -> shortestSequence(seq, depth - 1))
                        .min()
                        .orElseThrow();

                from = to + 1;
            }

            return total;
        }
    }

    private List<String> sequences(Multimap<Path, String> pathCache, String keys, int index, char previousKey, String currentPath) {
        if (index == keys.length()) {
            return List.of(currentPath);
        }

        var nextKey = keys.charAt(index);
        var result = Lists.<String>newArrayList();

        for (String path : pathCache.get(new Path(previousKey, nextKey))) {
            result.addAll(sequences(pathCache, keys, index + 1, nextKey, currentPath + path + 'A'));
        }

        return result;
    }

    record Path(char from, char to) {
    }

    private static Multimap<Path, String> pathMap(String input) {
        var keypad = Utils.matrix(input);
        var result = HashMultimap.<Path, String>create();
        char[] all = Chars.concat(keypad);
        for (char from : all) {
            for (char to : all) {
                if (from != ' ' && to != ' ') {
                    result.putAll(new Path(from, to), keypadSequence(keypad, from, to));
                }
            }
        }
        return result;
    }

    record State(Vector2 position, String path) {
    }

    private static Set<String> keypadSequence(char[][] map, char start, char target) {
        return keypadSequence(map, find(map, start), find(map, target));
    }

    private static Set<String> keypadSequence(char[][] map, Vector2 start, Vector2 target) {
        var frontier = Lists.<State>newLinkedList();
        frontier.add(new State(start, ""));

        var minimumDistance = Maps.<Vector2, Integer>newHashMap();
        minimumDistance.put(start, 0);

        var result = Sets.<String>newHashSet();

        while (!frontier.isEmpty()) {
            var current = frontier.poll();

            if (current.position().equals(target)) {
                result.add(current.path());
            }

            var next = Map.of(
                    "^", current.position().north(),
                    "v", current.position().south(),
                    ">", current.position().east(),
                    "<", current.position().west()
            );

            for (Map.Entry<String, Vector2> entry : next.entrySet()) {
                var neighbour = entry.getValue();
                var step = entry.getKey();
                if (contains(map, neighbour) && get(map, neighbour) != ' ') {
                    if (minimumDistance.getOrDefault(neighbour, Integer.MAX_VALUE) >= minimumDistance.get(current.position()) + 1) {
                        minimumDistance.put(neighbour, minimumDistance.get(current.position()) + 1);
                        frontier.add(new State(neighbour, current.path() + step));
                    }
                }
            }
        }

        return result;
    }
}
