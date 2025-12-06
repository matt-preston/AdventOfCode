package aoc.y2025;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2025, day = 5, name = "Cafeteria")
public class Day05Solution {

    private static final String MOCK = """
            3-5
            10-14
            16-20
            12-18
            
            1
            5
            8
            11
            17
            32
            """;

    private static final String MOCK2 = """
            1-10
            5-15
            8-12
            
            1
            5
            8
            11
            15
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(3, solve(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(681, solve(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(14, solvePt2(mockInput(MOCK)));
        assertEquals(15, solvePt2(mockInput(MOCK2)));
    }

    @Test
    public void part2() {
        assertEquals(348820208020395L, solvePt2(input(this)));
    }

    record Range(long from, long to) {
        public boolean contains(long i) {
            return i >= from && i <= to;
        }
    }

    private long solve(Input input) {
        var ranges = input.section(0).lines().stream()
                .map(this::range)
                .toList();

        return input.section(1).lines().stream()
                .mapToLong(Long::parseLong)
                .filter(l -> ranges.stream().anyMatch(range -> range.contains(l)))
                .count();
    }

    private long solvePt2(Input input) {
        var ranges = input.section(0).lines().stream()
                .map(this::range)
                .sorted(Comparator.comparingLong(o -> o.from))
                .collect(Collectors.toCollection(ArrayList::new));

        var totalSize = 0L;

        var first = ranges.removeFirst();
        var mergedFrom = first.from;
        var mergedTo = first.to;

        for (Range next : ranges) {
            if (next.from <= mergedTo + 1) {
                mergedTo = Math.max(next.to, mergedTo); // merge the ranges
            } else {
                totalSize += (mergedTo - mergedFrom + 1);
                mergedFrom = next.from;
                mergedTo = next.to;
            }
        }

        totalSize += mergedTo - mergedFrom + 1; // add the last range

        return totalSize;
    }

    private Range range(String range) {
        var bits = range.split("-");
        return new Range(Long.parseLong(bits[0]), Long.parseLong(bits[1]));
    }
}
