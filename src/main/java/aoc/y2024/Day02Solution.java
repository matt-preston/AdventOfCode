package aoc.y2024;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 2, name = "Red-Nosed Reports")
public class Day02Solution {

    private static final String MOCK = """
            7 6 4 2 1
            1 2 7 8 9
            9 7 6 2 1
            1 3 2 4 5
            8 6 4 4 1
            1 3 6 7 9
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(2, safeLevels(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(334, safeLevels(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(4, safeLevelsWithDampening(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(400, safeLevelsWithDampening(input(this)));
    }

    private long safeLevels(Input input) {
       return input.lines().stream()
               .map(Utils::parseNumbers)
               .filter(this::safe)
               .count();
    }

    private long safeLevelsWithDampening(Input input) {
        return input.lines().stream()
                .map(Utils::parseNumbers)
                .filter(this::safeWithDampening)
                .count();
    }

    private boolean safe(List<Long> report) {
        for (int i = 1; i < report.size(); i++) {
            var diff  = report.get(i) - report.get(i - 1);
            if (Math.abs(diff) == 0 || Math.abs(diff) > 3) {
                return false;  // must be within 1..3
            }

            if (i > 1) {
                var previousDiff = report.get(i - 1) - report.get(i - 2);
                if (diff * previousDiff < 0) {
                    return false;  // different signs
                }
            }
        }

        return true;
    }

    private boolean safeWithDampening(List<Long> report) {
        if (safe(report)) {
            return true;
        }

        // would be more efficient to just skip i in safe() to avoid
        // copying the list over and over
        for (int i = 0; i < report.size(); i++) {
            var copy = new ArrayList<>(report);
            copy.remove(i);

            if (safe(copy)) {
                return true;
            }
        }

        return false;
    }
}
