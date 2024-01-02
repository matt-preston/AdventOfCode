package aoc.y2023;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;


@AdventOfCode(year = 2023, day = 9, name = "Mirage Maintenance")
public class Day09Solution {

    public static final String MOCK = """
            0 3 6 9 12 15
            1 3 6 10 15 21
            10 13 16 21 30 45
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(114, solutionForPart1(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(1884768153, solutionForPart1(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(2, solutionForPart2(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(1031, solutionForPart2(input(this)));
    }

    private long solutionForPart1(final Input input) {
        return sumOfPredictions(input, identity());
    }

    private long solutionForPart2(final Input input) {
        return sumOfPredictions(input, this::reverse);  // reverse the input lists to predict the first, rather than last, values
    }

    private List<Long> reverse(final List<Long> input) {
        List<Long> output = new ArrayList<>(input);
        Collections.reverse(output);
        return output;
    }

    private long sumOfPredictions(final Input input, Function<List<Long>, List<Long>> function) {
        var sum = 0L;
        for (final String line : input.lines()) {
            final var originalNumber = Utils.parseNumbers(line);
            final var transformedNumber = function.apply(originalNumber);

            sum += predictNextValue(transformedNumber);
        }
        return sum;
    }

    private long predictNextValue(final List<Long> numbers) {
        if (end(numbers)) {
            return 0;
        }

        var nextSequence = Lists.<Long>newArrayList();
        for (int i = 0; i < numbers.size() - 1; i++) {
            final var value = numbers.get(i + 1) - numbers.get(i);
            nextSequence.add(value);
        }

        return numbers.get(numbers.size() - 1) + predictNextValue(nextSequence);
    }

    private boolean end(final List<Long> numbers) {
        for (final Long number : numbers) {
            if (number != 0L) {
                return false;
            }
        }
        return true;
    }
}
