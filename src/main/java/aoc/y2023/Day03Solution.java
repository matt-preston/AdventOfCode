package aoc.y2023;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2023, day = 3, name = "Gear Ratios")
public class Day03Solution {

    @Test
    public void part1WithMockData() {
        assertEquals(4361, sumOfPartNumbers(mockInput("""
                467..114..
                ...*......
                ..35..633.
                ......#...
                617*......
                .....+.58.
                ..592.....
                ......755.
                ...$.*....
                .664.598..
                """)));
    }

    @Test
    public void part1() {
        assertEquals(540131, sumOfPartNumbers(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(467835, sumOfGearRatios(mockInput("""
                467..114..
                ...*......
                ..35..633.
                ......#...
                617*......
                .....+.58.
                ..592.....
                ......755.
                ...$.*....
                .664.598..
                """)));
    }

    @Test
    public void part2() {
        assertEquals(86879020, sumOfGearRatios(input(this)));
    }

    private int sumOfPartNumbers(final Input input) {
        var sum = 0;

        final var lines = input.linesArray();
        for (int index = 0; index < lines.length; index++) {
            final var pattern = Pattern.compile("([*#+$@=&/%\\-])");
            for (final MatchResult match : pattern.matcher(lines[index]).results().toList()) {
                sum += adjacentPartNumbers(index, match.start(), lines).stream()
                        .mapToInt(i -> i)
                        .sum();
            }
        }

        return sum;
    }

    private int sumOfGearRatios(final Input input) {
        var sum = 0;

        final var lines = input.linesArray();
        for (int index = 0; index < lines.length; index++) {
            final var pattern = Pattern.compile("([*])");
            for (final MatchResult match : pattern.matcher(lines[index]).results().toList()) {
                final var partNumbers = adjacentPartNumbers(index, match.start(), lines);
                if (partNumbers.size() == 2) {
                    sum += (partNumbers.get(0) * partNumbers.get(1));
                }
            }
        }

        return sum;
    }

    private List<Integer> adjacentPartNumbers(int lineIndex, int offset, String[] lines) {
        final var aboveOrBelow = Stream.concat(
                findPartNumbers(lineIndex - 1, lines),
                findPartNumbers(lineIndex + 1, lines)
        ).filter(p -> p.start() - 1 <= offset && p.end() >= offset);

        final var adjacentTo = findPartNumbers(lines[lineIndex])
                .filter(p -> p.end() == offset || p.start() - 1 == offset);

        return Stream.concat(aboveOrBelow, adjacentTo).
                mapToInt(p -> Integer.parseInt(p.group(0)))
                .boxed()
                .toList();
    }

    private Stream<MatchResult> findPartNumbers(final int lineIndex, String[] lines) {
        if (lineIndex < 0 || lineIndex >= lines.length) {
            return Stream.empty();
        }
        return findPartNumbers(lines[lineIndex]);
    }

    private Stream<MatchResult> findPartNumbers(final String string) {
        return Pattern.compile("\\d+").matcher(string).results();
    }
}
