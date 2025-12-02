package aoc.y2020;

import utils.AdventOfCode;
import org.junit.jupiter.api.Test;
import utils.Input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2020, day = 9, name = "Encoding Error")
public class Day09Solution {

    private static final String MOCK = """
            35
            20
            15
            25
            47
            40
            62
            55
            65
            95
            102
            117
            150
            182
            127
            219
            299
            277
            309
            576
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(127, solve(mockInput(MOCK), 5));
    }

    @Test
    public void part1() {
        assertEquals(507622668, solve(input(this), 25));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(62, encryptionWeakness(mockInput(MOCK), 127));
    }

    @Test
    public void part2() {
        assertEquals(0, encryptionWeakness(input(this), 507622668));
    }

    private int solve(Input input, int preamble) {
        var buffer = new CircularBuffer(preamble);

        input.lines().stream()
                .limit(preamble)
                .forEach(s -> buffer.add(Integer.parseInt(s)));

        return input.lines().stream()
                .skip(preamble)
                .mapToInt(Integer::parseInt)
                .filter(i -> !sumOfTwoNumbers(i, buffer))
                .findFirst()
                .orElseThrow();
    }

    private long encryptionWeakness(Input input, int invalidNumber) {
        var numbers = input.lines().stream()
                .mapToLong(Long::parseLong)
                .toArray();

        for (int i = 0; i < numbers.length; i++) {
            var total = numbers[i];
            var smallest = numbers[i];
            var largest = numbers[i];
            for (int j = i + 1; j < numbers.length; j++) {
                total += numbers[j];
                if (numbers[j] < smallest) {
                    smallest = numbers[j];
                }
                if (numbers[j] > largest) {
                    largest = numbers[j];
                }

                if (total == invalidNumber) {
                    return smallest + largest;
                } else if (total > invalidNumber) {
                    break;
                }
            }
        }

        return 0;
    }

    private boolean sumOfTwoNumbers(int value, CircularBuffer buffer) {
        for (int i = 0; i < buffer.size(); i++) {
            for (int j = 0; j < buffer.size(); j++) {
                if (i != j) {
                    if (buffer.sum(i, j) == value) {
                        buffer.add(value);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static class CircularBuffer {
        private final int[] buffer;
        private int end = 0;

        public CircularBuffer(int size) {
            buffer = new int[size];
        }

        public void add(int value) {
            buffer[end++ % buffer.length] = value;
        }

        public int size() {
            return buffer.length;
        }

        public int sum(int a, int b) {
            return buffer[a] + buffer[b];
        }

    }
}
