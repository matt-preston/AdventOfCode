package aoc.y2024;

import utils.AdventOfCode;
import org.junit.jupiter.api.Test;
import utils.Input;

import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 3, name = "Mull It Over")
public class Day03Solution {


    private static final String MOCK_1 = """
            xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))
            """;

    private static final String MOCK_2 = """
            xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(161, sumMul(mockInput(MOCK_1)));
    }

    @Test
    public void part1() {
        assertEquals(188741603, sumMul(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(48, sumWithConditionals(mockInput(MOCK_2)));
    }

    @Test
    public void part2() {
        assertEquals(67269798, sumWithConditionals(input(this)));
    }

    private int sumMul(Input input) {
        return sum(input, Pattern.compile("mul\\((?<l>\\d{1,3}),(?<r>\\d{1,3})\\)"));
    }

    private int sumWithConditionals(Input input) {
        return sum(input, Pattern.compile("(don't\\(\\))|(do\\(\\))|(mul\\((?<l>\\d{1,3}),(?<r>\\d{1,3})\\))"));
    }

    private int sum(Input input, Pattern pattern) {
        var matcher = pattern.matcher(input.text());
        var result = 0;
        var enabled = true;

        while(matcher.find()) {
            var match = matcher.group(0);
            if (enabled && match.startsWith("mul")) {
                result += (parseInt(matcher.group("l")) * parseInt(matcher.group("r")));
            } else if (match.startsWith("don")) {
                enabled = false;
            } else if (match.startsWith("do(")) {
                enabled = true;
            }
        }
        return result;
    }
}
