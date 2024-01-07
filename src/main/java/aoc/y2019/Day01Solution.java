package aoc.y2019;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;

@AdventOfCode(year = 2019, day = 1, name = "The Tyranny of the Rocket Equation")
public class Day01Solution {

    @Test
    public void part1() {
        assertEquals(3474920, input(this).lines().stream()
                .mapToInt(Integer::parseInt)
                .map(this::fuelNeeded)
                .sum());
    }

    @Test
    public void part2() {
        assertEquals(5209504, input(this).lines().stream()
                .mapToInt(Integer::parseInt)
                .map(this::fuelNeededRecursive)
                .sum());
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
