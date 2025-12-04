package aoc.y2020;

import com.google.common.collect.Lists;
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

    private static final String MOCK2 = """
            939
            67,7,x,59,61
            """;

    private static final String MOCK3 = """
            939
            1789,37,47,1889
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
        assertEquals(1068781, solvePt2(mockInput(MOCK)));
        assertEquals(1261476, solvePt2(mockInput(MOCK2)));
        assertEquals(1202161486, solvePt2(mockInput(MOCK3)));
    }

    @Test
    public void part2() {
        assertEquals(775230782877242L, solvePt2(input(this)));
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
                        (i, _) -> i,
                        TreeMap::new));

        return times.firstEntry().getValue() * (times.firstKey() - earliest);
    }

    record Constraint(long remainder, long mod) {
    }

    private long solvePt2(Input input) {
        var busIds = input.linesArray()[1].split(",");

        var constraints = Lists.<Constraint>newArrayList();

        for (int i = 0; i < busIds.length; i++) {
            if (!busIds[i].equals("x")) {
                var mod = Integer.parseInt(busIds[i]);
                var remainder = (mod - i) % mod;
                constraints.add(new Constraint(remainder, mod));
            }
        }

        return constraints.stream()
                .reduce(this::merge)
                .orElseThrow()
                .remainder();
    }

    // Chinese Remainder Theory
    private Constraint merge(Constraint c1, Constraint c2) {
        long a = c1.remainder;
        long m = c1.mod;
        long b = c2.remainder;
        long n = c2.mod;

        // Extended GCD
        long[] egcd = extendedGCD(m, n); // returns {g, x, y} s.t. g = m*x + n*y
        long g = egcd[0];
        long x = egcd[1];

        // Check for solvability
        if ((b - a) % g != 0) {
            throw new IllegalArgumentException("No solution exists for these two constraints");
        }

        // lcm of m and n
        long lcm = m / g * n;

        // Compute multiplier
        long k = ((b - a) / g * x) % (n / g);
        if (k < 0) {
            k += n / g; // ensure non-negative
        }

        // Compute merged remainder
        long r = (a + m * k) % lcm;
        if (r < 0) {
            r += lcm;
        }

        return new Constraint(r, lcm);
    }

    // Extended GCD: returns array {g, x, y} such that g = a*x + b*y
    private long[] extendedGCD(long a, long b) {
        if (b == 0) return new long[]{a, 1, 0};
        long[] vals = extendedGCD(b, a % b);
        long g = vals[0];
        long x = vals[2];
        long y = vals[1] - (a / b) * vals[2];
        return new long[]{g, x, y};
    }
}
