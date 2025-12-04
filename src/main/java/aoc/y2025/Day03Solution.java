package aoc.y2025;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2025, day = 3, name = "Lobby")
public class Day03Solution {

    private static final String MOCK = """
            987654321111111
            811111111111119
            234234234234278
            818181911112111
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(357, solve(mockInput(MOCK), 2));
    }

    @Test
    public void part1() {
        assertEquals(17324, solve(input(this), 2));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(3121910778619L, solve(mockInput(MOCK), 12));
    }

    @Test
    public void part2() {
        assertEquals(171846613143331L, solve(input(this), 12));
    }

    private long solve(Input input, int numDigits) {
        return input.lines().stream()
                .mapToLong(s -> maxJoltage(s, numDigits))
                .sum();
    }

    private long maxJoltage(String string, int numDigits) {
        var ints = string.chars()
                .map(i -> Integer.parseInt(Character.toString((char) i)))
                .toArray();

        var digits = new int[numDigits];

        var prevIndex = -1;
        for (int digit = 0; digit < numDigits; digit++) {
            var index = indexOfMax(ints, prevIndex + 1, ints.length - (numDigits - (digit + 1)));
            digits[digit] = ints[index];
            prevIndex = index;
        }

        var total = 0L;
        for (int i = 0; i < digits.length; i++) {
            var next = digits[i];
            total += (long) (next * Math.pow(10, digits.length - (i + 1)));
        }

        return total;
    }

    private int indexOfMax(int[] input, int from, int to) {
        var max = input[from];
        var result = from;

        for (int i = from; i < to; i++) {
            var next = input[i];
            if (next > max) {
                max = next;
                result = i;
            }
        }

        return result;
    }
}
