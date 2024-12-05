package aoc.y2023;

import com.google.common.base.Preconditions;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;
import static utils.Utils.parseLongs;

@AdventOfCode(year = 2023, day = 6, name = "Wait For It")
public class Day06Solution {

    @Test
    public void part1WithMockData() {
        assertEquals(288, solutionForPart1(mockInput("""
                Time:      7  15   30
                Distance:  9  40  200
                """
        )));
    }

    @Test
    public void part1() {
        assertEquals(1155175, solutionForPart1(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(71503, solutionForPart2(mockInput("""
                Time:      7  15   30
                Distance:  9  40  200
                """
        )));
    }

    @Test
    public void part2() {
        assertEquals(35961505, solutionForPart2(input(this)));
    }

    public long solutionForPart1(final Input input) {
        final var lines = input.linesArray();

        final List<Long> times = Utils.parseLongs(lines[0].substring(10));
        final List<Long> distances = Utils.parseLongs(lines[1].substring(10));

        Preconditions.checkState(times.size() == distances.size());

        var total = 1L;
        for (int i = 0; i < times.size(); i++) {
            long time = times.get(i);
            long distance = distances.get(i);
            total *= numberOfTimesBeats(time, distance);
        }

        return total;
    }

    public long solutionForPart2(final Input input) {
        final var lines = input.linesArray();

        var fullTime = Long.parseLong(lines[0].substring(10).replaceAll("\\s", ""));
        var fullDistance = Long.parseLong(lines[1].substring(10).replaceAll("\\s", ""));

        return numberOfTimesBeats(fullTime, fullDistance);
    }

    private int numberOfTimesBeats(final long time, final long record) {
        var count = 0;
        for (int hold = 0; hold <= time; hold++) {
            var remaining = time - hold;
            var distance = remaining * hold;

            if (distance > record) {
                count++;
            }
        }

        return count;
    }

}
