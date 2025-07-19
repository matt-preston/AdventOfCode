package aoc.y2022;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2022, day = 5, name = "Supply Stacks")
public class Day05Solution {

    private static final String MOCK = """
                [D]
            [N] [C]
            [Z] [M] [P]
             1   2   3
            
            move 1 from 2 to 1
            move 3 from 1 to 3
            move 2 from 2 to 1
            move 1 from 1 to 2
            """;

    @Test
    public void part1WithMockData() {
        assertEquals("CMZ", solvePart1(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals("WSFTMRHPP", solvePart1(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals("MCD", solvePart2(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals("GSLCMFBRP", solvePart2(input(this)));
    }

    private String solvePart1(Input input) {
        return solve(input, List::reversed);
    }

    private String solvePart2(Input input) {
        return solve(input, crates -> crates);
    }

    private String solve(Input input, UnaryOperator<List<String>> operator) {
        var cratesSection = input.section(0).linesArray();

        var lastLine = cratesSection[cratesSection.length - 1];
        int numStacks = Integer.parseInt(lastLine.substring(lastLine.length() - 1));

        var stacks = new ArrayList<List<String>>(numStacks);
        for (int i = 0; i < numStacks; i++) {
            stacks.add(new ArrayList<>());
        }

        for (int i = cratesSection.length - 2; i >= 0; i--) {
            for (int j = 0; j < numStacks; j++) {
                var offset = (j * 4) + 1;
                if (cratesSection[i].length() >= offset) {
                    var c = cratesSection[i].charAt(offset);
                    if (c != ' ') {
                        stacks.get(j).add(String.valueOf(c));
                    }
                }
            }
        }

        var pattern = Pattern.compile("move (\\d+) from (\\d+) to (\\d+)");

        for (String move : input.section(1).lines()) {
            var matcher = pattern.matcher(move);
            if (!matcher.matches()) {
                throw new IllegalStateException();
            }

            var count = Integer.parseInt(matcher.group(1));
            var from = Integer.parseInt(matcher.group(2)) - 1;
            var to = Integer.parseInt(matcher.group(3)) - 1;

            var fromCrates = stacks.get(from);

            var keepCrates = fromCrates.subList(0, fromCrates.size() - count);
            var moveCrates = fromCrates.subList(fromCrates.size() - count, fromCrates.size());

            stacks.set(from, keepCrates);
            stacks.get(to).addAll(operator.apply(moveCrates));
        }

        return stacks.stream()
                .map(List::getLast)
                .reduce("", (s, s2) -> s + s2);
    }
}
