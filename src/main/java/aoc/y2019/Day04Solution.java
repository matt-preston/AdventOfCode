package aoc.y2019;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;

import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.*;

@AdventOfCode(year = 2019, day = 4, name = "Secure Container")
public class Day04Solution {

    @Test
    public void part1WithMockData() {
        assertTrue(validWithPart1Rules(111111));
        assertFalse(validWithPart1Rules(223450));
        assertFalse(validWithPart1Rules(123789));
    }

    @Test
    public void part1() {
        assertEquals(921, range(278384, 824795)
                .filter(this::validWithPart1Rules)
                .count());

    }

    @Test
    public void part2WithMockData() {
        assertTrue(validWithPart2Rules(112233));
        assertFalse(validWithPart2Rules(123444));
        assertTrue(validWithPart2Rules(111122));
    }

    @Test
    public void part2() {
        assertEquals(603, range(278384, 824795)
                .filter(this::validWithPart2Rules)
                .count());
    }

    private boolean validWithPart1Rules(int value) {
        final var charArray = String.valueOf(value).toCharArray();
        var previous = charArray[0];
        var hasEquals = false;

        for (int i = 1; i < charArray.length; i++) {
            var next = charArray[i];
            if (next < previous) {
                return false;
            } else if (next == previous) {
                hasEquals = true;
            }
            previous = next;
        }

        return hasEquals;
    }

    private boolean validWithPart2Rules(int value) {
        final var charArray = String.valueOf(value).toCharArray();
        var previous = charArray[0];
        var contiguous = 1;
        var foundExactly2 = false;

        for (int i = 1; i < charArray.length; i++) {
            var next = charArray[i];
            if (next < previous) {
                return false;
            } else if (next == previous) {
                contiguous++;
            } else {
                if (contiguous == 2) {
                    foundExactly2 = true;
                }
                contiguous = 1;
            }
            previous = next;
        }

        if (contiguous == 2) {
            foundExactly2 = true;
        }

        return foundExactly2;
    }


}
