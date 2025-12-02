package aoc.y2020;

import utils.AdventOfCode;
import org.junit.jupiter.api.Test;
import utils.Input;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2020, day = 1, name = "Report Repair")
public class Day01Solution {

    private static final String MOCK = """
            1721
            979
            366
            299
            675
            1456
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(514579, part1(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(259716, part1(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(241861950, part2(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(120637440, part2(input(this)));
    }

    private int part1(Input input) {
        return expenseReport(numbers(input), 2020);
    }

    private int part2(Input input) {
        var numbers = numbers(input);
        while(!numbers.isEmpty()) {
            var first = numbers.removeLast();
            var total = expenseReport(numbers, 2020 - first);
            if (total > 0) {
                return total * first;
            }
        }

        return 0;
    }

    private List<Integer> numbers(Input input) {
        return input.lines().stream()
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    private int expenseReport(Collection<Integer> numbers, int target) {
        for (Integer number : numbers) {
            var candidate = target - number;
            if (numbers.contains(candidate)) {
                return number * candidate;
            }
        }

        return 0;
    }
}
