package aoc.y2019;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;

@AdventOfCode(year = 2019, day = 1, name = "The Tyranny of the Rocket Equation")
public class Day01Solution {

    @Test
    public void part1() {
        var total = 0;
        for (String line : input(this).lines()) {
            total += fuelNeeded(Integer.parseInt(line));
        }
        assertEquals(3474920, total);
    }

    @Test
    public void part2WithMockData() {
        assertEquals(2, fuelNeededRecursive(14));
        assertEquals(966, fuelNeededRecursive(1969));
        assertEquals(50346, fuelNeededRecursive(100756));
    }

    @Test
    public void part2() {
        var total = 0;
        for (String line : input(this).lines()) {
            total += fuelNeededRecursive(Integer.parseInt(line));
        }
        assertEquals(5209504, total);
    }

    private int fuelNeeded(int mass) {
        return (mass / 3) - 2;
    }

    private int fuelNeededRecursive(int mass) {
        final var fuel = Math.max(fuelNeeded(mass), 0);

        if (fuel > 0) {
            return fuel + fuelNeededRecursive(fuel);
        } else {
            return fuel;
        }
    }
}
