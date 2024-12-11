package aoc.y2024;

import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 11, name = "Plutonian Pebbles")
public class Day11Solution {

    private static final String MOCK = "125 17";

    @Test
    public void part1WithMockData() {
        assertEquals(55312, sumOfStones(mockInput(MOCK), 25));
    }

    @Test
    public void part1() {
        assertEquals(182081, sumOfStones(input(this), 25));
    }

    @Test
    public void part2() {
        assertEquals(216318908621637L, sumOfStones(input(this), 75));
    }

    private long sumOfStones(Input input, int times) {
        return Utils.parseLongs(input.text()).stream()
                .mapToLong(stone -> blink(stone, times))
                .sum();
    }

    record Key(long stone, int depth) {
    }

    private static Map<Key, Long> CACHE = Maps.newHashMap();

    private long blink(long stone, int depth) {
        var key = new Key(stone, depth);
        var result = CACHE.get(key);
        if (result == null) {
            result = blinkImpl(stone, depth);
            CACHE.put(key, result);
        }
        return result;
    }

    private long blinkImpl(long stone, int depth) {
        if (depth == 0) {
            return 1;
        }

        if (stone == 0) {
            return blink(1, depth - 1);
        } else if (((int) (Math.log10(stone)) + 1) % 2 == 0) {  // even digits
            int len = (int) (Math.log10(stone) + 1);

            var first = (long) (stone / Math.pow(10, len / 2));
            var second = (long) (stone - (first * Math.pow(10, len / 2)));

            return blink(first, depth - 1) + blink(second, depth - 1);
        } else {
            return blink(stone * 2024L, depth - 1);
        }
    }
}
