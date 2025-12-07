package aoc.y2020;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2020, day = 15, name = "Rambunctious Recitation")
public class Day15Solution {

    @Test
    public void part1WithMockData() {
        assertEquals(436, solve(mockInput("0,3,6")));
    }

    @Test
    public void part1() {
        assertEquals(410, solve(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(175594, solvePt2(mockInput("0,3,6")));
    }

    @Test
    public void part2() {
        assertEquals(238, solvePt2(input(this)));
    }

    private int solve(Input input) {
        return solve(input, 2020);
    }

    private int solvePt2(Input input) {
        return solve(input, 30_000_000);
    }

    private int solve(Input input, int max) {
        var valueToLastIndex = new int[max];
        Arrays.fill(valueToLastIndex, -1);

        var startingNumbers = Utils.parseInts(input.text());

        var turn = 1;
        var lastValue = startingNumbers.getFirst();
        for (int i = 1; i < startingNumbers.size(); i++) {
            valueToLastIndex[lastValue] = turn++;
            lastValue = startingNumbers.get(i);
        }

        while (turn < max) {
            turn++;
            if (valueToLastIndex[lastValue] != -1) {
                var previousIndex = valueToLastIndex[lastValue];
                var newValue = turn - 1 - previousIndex;

                valueToLastIndex[lastValue] = turn - 1;
                lastValue = newValue;
            } else {
                valueToLastIndex[lastValue] = turn - 1;
                lastValue = 0;
            }
        }

        return lastValue;
    }
}
