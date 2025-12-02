package aoc.y2020;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2020, day = 13, name = "Shuttle Search")
public class Day13Solution {

    private static final String MOCK = """
            939
            7,13,x,x,59,x,31,19
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(295, solve(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(8063, solve(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(0, solve(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(0, solve(input(this)));
    }

    private int solve(Input input) {
        var lines = input.linesArray();

        var earliest = Integer.parseInt(lines[0]);
        var times = Arrays.stream(lines[1].split(","))
                .filter(s -> !s.equals("x"))
                .map(Integer::parseInt)
                .collect(Collectors.toMap(
                        i -> (int) Math.ceil(earliest / (double) i) * i,
                        i -> i,
                        (i, i2) -> i,
                        TreeMap::new));

        return times.firstEntry().getValue() * (times.firstKey() - earliest);
    }
}
