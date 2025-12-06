package aoc.y2019;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2019, day = 16, name = "Flawed Frequency Transmission")
public class Day16Solution {

    @Test
    public void part1WithSimpleMockData() {
        assertEquals(1029498, solve(mockInput("12345678"), 4));
    }

    @Test
    public void part1WithMockData() {
        assertEquals(24176176, solve(mockInput("80871224585914546619083218645595"), 100));
        assertEquals(73745418, solve(mockInput("19617804207202209144916044189917"), 100));
        assertEquals(52432133, solve(mockInput("69317163492948606335995924319873"), 100));
    }

    @Test
    public void part1() {
        assertEquals(82435530, solve(input(this), 100));
    }

    @Test
    public void part2WithMockData() {
        fail();
        assertEquals(84462026, solve(mockInput("03036732577212944063491565474664"), 100, 10_000));
    }

    @Test
    public void part2() {
        fail();
        assertEquals(0, solve(input(this), 100, 10_000));
    }

    private long solve(Input input, int phases) {
        return solve(input, phases, 1);
    }

    private long solve(Input input, int phases, int repeat) {
        var elements = input.text().trim().repeat(repeat).chars()
                .map(c -> Integer.parseInt("" + ((char) c)))
                .toArray();

        for (int phase = 0; phase < phases; phase++) {
            elements = phase(elements);
        }

        var result = 0L;

        for (int i = 0; i < 8; i++) {
            long result1 = elements[i] * (long) Math.pow(10, 7 - i);
            result += result1;
        }

        return result;
    }

    static class Pattern {
        private final int element;
        private int count;

        public Pattern(int element) {
            this.element = element;
            this.count = 1; // skip first
        }

        public int multiplier() {
            var result = this.count / this.element;
            this.count = (this.count + 1) % (element * 4);
            return switch(result) {
                case 0, 2 -> 0;
                case 1 -> 1;
                case 3 -> -1;
                default -> throw new IllegalStateException();
            };
        }
    }

    private int[] phase(int[] input) {
        var result = new int[input.length];
        for (int outputElement = 0; outputElement < input.length; outputElement++) {
            var pattern = new Pattern(outputElement + 1);

            var sum = 0;
            for (int j : input) {
                sum += j * pattern.multiplier();
            }

            result[outputElement] = leastSignificantDigit(sum);
        }
        return result;
    }

    private int leastSignificantDigit(long value) {
        return (int) Math.abs(value - ((value / 10) * 10));
    }
}
