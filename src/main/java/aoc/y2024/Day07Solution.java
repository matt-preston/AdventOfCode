package aoc.y2024;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;

import java.util.List;

import static java.lang.Long.parseLong;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 7, name = "Bridge Repair")
public class Day07Solution {

    private static final String MOCK = """
            190: 10 19
            3267: 81 40 27
            83: 17 5
            156: 15 6
            7290: 6 8 6 15
            161011: 16 10 13
            192: 17 8 14
            21037: 9 7 18 13
            292: 11 6 16 20
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(3749, sumValidEquations(mockInput(MOCK), false));
    }

    @Test
    public void part1() {
        assertEquals(3312271365652L, sumValidEquations(input(this), false));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(11387, sumValidEquations(mockInput(MOCK), true));
    }

    @Test
    public void part2() {
        assertEquals(509463489296712L, sumValidEquations(input(this), true));
    }

    record Equation(long result, List<Long> values) {
    }

    private long sumValidEquations(Input input, boolean pt2) {
        var sum = 0L;
        for (Equation equation : parse(input)) {
            if (valid(equation, equation.values().getFirst(), skipFirst(equation.values()), pt2)) {
                sum += equation.result();
            }
        }
        return sum;
    }

    private boolean valid(Equation equation, long left, List<Long> remaining, boolean pt2) {
        if (remaining.isEmpty()) {
            return left == equation.result();
        }

        var right = remaining.getFirst();

        return valid(equation, left + right, skipFirst(remaining), pt2)
                || valid(equation, left * right, skipFirst(remaining), pt2)
                || (pt2 && valid(equation, parseLong(left + Long.toString(right)), skipFirst(remaining), pt2));
    }

    private List<Long> skipFirst(List<Long> list) {
        return list.subList(1, list.size());
    }

    private List<Equation> parse(Input input) {
        var result = Lists.<Equation>newArrayList();
        for (String line : input.lines()) {
            List<Long> longs = Utils.parseLongs(line);
            result.add(new Equation(longs.getFirst(), longs.subList(1, longs.size())));
        }
        return result;
    }
}
