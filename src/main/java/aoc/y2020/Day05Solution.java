package aoc.y2020;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2020, day = 5, name = "Binary Boarding")
public class Day05Solution {

    private static final String MOCK = """
            FBFBBFFRLR
            BFFFBBFRRR
            FFFBBBFRRR
            BBFFBBFRLL
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(820, maxSeatId(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(838, maxSeatId(input(this)));
    }

    @Test
    public void part2() {
        assertEquals(714, missingSeatId(input(this)));
    }

    private int maxSeatId(Input input) {
        return input.lines().stream()
                .mapToInt(this::seatId)
                .max()
                .orElse(0);
    }

    private int missingSeatId(Input input) {
        var s = input.lines().stream()
                .map(this::seatId)
                .collect(Collectors.toCollection(TreeSet::new));

        var prev = s.first() - 1;
        for (Integer next : s) {
            if (prev + 1 == next) {
                prev = next;
            } else {
                return prev + 1;
            }
        }

        return 0;
    }

    private int seatId(String line) {
        return (search(line, 0, 127) * 8) + search(line.substring(7), 0, 7);
    }

    private int search(String commands, int lo, int high) {
        if (lo == high) {
            return lo;
        }

        if (commands.charAt(0) == 'F' || commands.charAt(0) == 'L') {
            return search(commands.substring(1), lo, lo + ((high - lo - 1) / 2));
        } else {
            return search(commands.substring(1), lo + ((high - lo + 1) / 2), high);
        }
    }
}
