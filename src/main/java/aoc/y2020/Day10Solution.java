package aoc.y2020;

import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2020, day = 10, name = "Adapter Array")
public class Day10Solution {

    private static final String MOCK = """
            16
            10
            15
            5
            1
            11
            7
            19
            6
            12
            4
            """;

    private static final String MOCK2 = """
            28
            33
            18
            42
            31
            14
            46
            20
            48
            47
            24
            23
            49
            45
            19
            38
            39
            11
            1
            32
            25
            35
            8
            17
            7
            9
            4
            2
            34
            10
            3
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(35, solve(mockInput(MOCK)));
        assertEquals(220, solve(mockInput(MOCK2)));
    }

    @Test
    public void part1() {
        assertEquals(1917, solve(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(8, countValidArrangements(mockInput(MOCK)));
        assertEquals(19208, countValidArrangements(mockInput(MOCK2)));
    }

    @Test
    public void part2() {
        assertEquals(113387824750592L, countValidArrangements(input(this)));
    }

    private int solve(Input input) {
        var jolts = input.lines().stream()
                .map(Integer::parseInt)
                .collect(Collectors.toCollection(TreeSet::new));

        jolts.add(jolts.last() + 3);

        var differences = new HashMap<Integer, Integer>();

        var prev = 0;
        for (Integer jolt : jolts) {
            differences.compute(jolt - prev, (k, v) -> (v == null) ? 1 : v + 1);
            prev = jolt;
        }

        return differences.get(1) * differences.get(3);
    }

    private long countValidArrangements(Input input) {
        var jolts = input.lines().stream()
                .map(Integer::parseInt)
                .collect(Collectors.toCollection(TreeSet::new));

        jolts.add(jolts.last() + 3);

        return countValidArrangements(0, jolts) / 2;
    }

    private Map<String, Long> cache = Maps.newHashMap();

    private long countValidArrangements(int previous, TreeSet<Integer> jolts) {
        var key = previous + ":" + jolts;
        var result = cache.get(key);
        if (result == null) {
            result = countValidArrangementsImpl(previous, jolts);
            cache.put(key, result);
        }
        return result;
    }

    private long countValidArrangementsImpl(int previous, TreeSet<Integer> jolts) {
        if (jolts.isEmpty()) {
            return 1;
        }

        var copy = new TreeSet<>(jolts);

        var first = copy.removeFirst();
        if (first - previous < 4) {
            return countValidArrangements(first, copy) +
                    countValidArrangements(previous, copy);
        }
        return 0;
    }
}
