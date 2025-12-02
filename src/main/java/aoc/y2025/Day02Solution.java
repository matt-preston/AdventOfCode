package aoc.y2025;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2025, day = 2, name = "Gift Shop")
public class Day02Solution {

    private static final String MOCK = """
            11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(1227775554, solve(mockInput(MOCK), this::simpleRepeating));
    }

    @Test
    public void part1() {
        assertEquals(28844599675L, solve(input(this), this::simpleRepeating));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(4174379265L, solve(mockInput(MOCK), this::complexRepeating));
    }

    @Test
    public void part2() {
        assertEquals(48778605167L, solve(input(this), this::complexRepeating));
    }

    interface Checker {
        boolean repeating(long id);
    }

    private long solve(Input input, Checker checker) {
        return Arrays.stream(input.text().trim().split(","))
                .mapToLong(s -> invalidIds(s, checker))
                .sum();
    }

    private long invalidIds(String range, Checker checker) {
        var bits = range.split("-");
        var from = Long.parseLong(bits[0]);
        var to = Long.parseLong(bits[1]);

        var sum = 0L;

        for (long i = from; i <= to; i++) {
            if (checker.repeating(i)) {
                sum += i;
            }
        }

        return sum;
    }

    private boolean simpleRepeating(long id) {
        var idString = Long.toString(id);

        var p1 = idString.substring(0, (idString.length() / 2));
        var p2 = idString.substring(idString.length() / 2);

        return p1.equals(p2);
    }

    private boolean complexRepeating(long id) {
        var idString = Long.toString(id);

        for (int i = 1; i <= idString.length() / 2; i++) {
            if (idString.length() % i == 0) { // valid check
                var substr = idString.substring(0, i);
                var repeat = idString.length() / i;
                var result = substr.repeat(repeat);
                if (result.equals(idString)) {
                    return true;
                }
            }
        }

        return false;
    }
}
