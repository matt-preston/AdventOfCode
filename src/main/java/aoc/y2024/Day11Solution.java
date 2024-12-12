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

    private long sumOfStones(Input input, int blinks) {
        return Utils.parseLongs(input.text()).stream()
                .mapToLong(stone -> numberOfStones(stone, blinks))
                .sum();
    }

    record Key(long stone, int blinks) {
    }

    private final static Map<Key, Long> CACHE = Maps.newHashMap();

    private long numberOfStones(long value, int numBlinks) {
        var key = new Key(value, numBlinks);
        var result = CACHE.get(key);
        if (result == null) {
            result = numberOfStonesImpl(value, numBlinks);
            CACHE.put(key, result);
        }
        return result;
    }

    private long numberOfStonesImpl(long value, int numBlinks) {
        if (numBlinks == 0) {
            return 1;
        }

        if (value == 0) {
            return numberOfStones(1, numBlinks - 1);
        } else if (((int) (Math.log10(value)) + 1) % 2 == 0) {  // even digits
            int len = (int) (Math.log10(value) + 1);

            var first = (long) (value / Math.pow(10, len / 2));
            var second = (long) (value - (first * Math.pow(10, len / 2)));

            return numberOfStones(first, numBlinks - 1) + numberOfStones(second, numBlinks - 1);
        } else {
            return numberOfStones(value * 2024L, numBlinks - 1);
        }
    }
}
