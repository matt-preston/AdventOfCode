package aoc.y2025;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Section;
import utils.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;

@AdventOfCode(year = 2025, day = 12, name = "Christmas Tree Farm")
public class Day12Solution {

    @Test
    public void part1() {
        assertEquals(497, solve(input(this)));
    }

    // This doesn't work for the test input, but oddly does for the real input...
    private int solve(Input input) {
        var allSections = input.sections();

        var sizes = allSections.stream()
                .limit(allSections.size() - 1)
                .mapToInt(this::countHash)
                .toArray();

        var fits = 0;

        for (String line : allSections.getLast().lines()) {
            var bits = line.split(":");
            var dimensions = Utils.parseInts(bits[0], "x");
            var counts = Utils.parseInts(bits[1]);

            var needed = 0;
            for (int i = 0; i < counts.size(); i++) {
                needed += counts.get(i) * sizes[i];
            }

            if (dimensions.get(0) * dimensions.get(1) >= needed) {
                fits++;
            }
        }

        return fits;
    }

    private int countHash(Section section) {
        var str = section.text();
        return str.length() - str.replace("#", "").length();
    }
}
