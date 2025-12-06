package aoc.y2025;

import com.google.common.collect.Lists;
import utils.AdventOfCode;
import org.junit.jupiter.api.Test;
import utils.Input;
import utils.Utils;

import java.util.function.BinaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2025, day = 6, name = "Trash Compactor")
public class Day06Solution {

    private static final String MOCK = """
           123 328  51 64\s
            45 64  387 23\s
             6 98  215 314
             *   +   *   +
           """;

    @Test
    public void part1WithMockData() {
        assertEquals(4277556, solve(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(3261038365331L, solve(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(3263827, solvePt2(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(8342588849093L, solvePt2(input(this)));
    }

    private long solve(Input input) {
        var rows = input.lines().stream()
                .map(s -> s.trim().split("\\s+"))
                .toArray(String[][]::new);

        var sum = 0L;

        for (int column = 0; column < rows[0].length; column++) {
            var op = rows[rows.length - 1][column];

            var numbers = Lists.<Long>newArrayList();

            for (int r = 0; r < rows.length - 1; r++) {
                numbers.add(Long.parseLong(rows[r][column]));
            }

            sum += numbers.stream().reduce(calculate(op)).orElse(0L);
        }

        return sum;
    }

    private long solvePt2(Input input) {
        // rotate CCW...
        var matrix = Utils.rotateCW(Utils.rotateCW(Utils.rotateCW(Utils.matrix(input))));

        var sum = 0L;

        var operator = "";
        var numbers = Lists.<Long>newArrayList();

        for (char[] line : matrix) {
            var s = new String(line).trim();
            if (s.isEmpty()) {
                sum += numbers.stream().reduce(calculate(operator)).orElse(0L);
                numbers.clear();
            } else if (s.endsWith("+") || s.endsWith("*")) {
                operator = s.substring(s.length() - 1);
                numbers.add(Long.parseLong(s.substring(0, s.length() - 1).trim()));
            } else {
                numbers.add(Long.parseLong(s));
            }
        }

        // add the last one
        sum += numbers.stream().reduce(calculate(operator)).orElse(0L);

        return sum;
    }

    private static BinaryOperator<Long> calculate(String operator) {
        return (l, l2) -> operator.equals("+") ? l + l2 : l * l2;
    }
}
