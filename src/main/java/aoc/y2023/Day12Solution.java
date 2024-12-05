package aoc.y2023;

import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2023, day = 12, name = "Hot Springs")
public class Day12Solution {

    @Test
    public void part1WithMockData() {
        assertEquals(21, sumOfArrangements(mockInput("""
                ???.### 1,1,3
                .??..??...?##. 1,1,3
                ?#?#?#?#?#?#?#? 1,3,1,6
                ????.#...#... 4,1,1
                ????.######..#####. 1,6,5
                ?###???????? 3,2,1
                """), 1));
    }

    @Test
    public void part1() {
        assertEquals(6958, sumOfArrangements(input(this), 1));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(525152, sumOfArrangements(mockInput("""
                ???.### 1,1,3
                .??..??...?##. 1,1,3
                ?#?#?#?#?#?#?#? 1,3,1,6
                ????.#...#... 4,1,1
                ????.######..#####. 1,6,5
                ?###???????? 3,2,1
                """), 5));
    }

    @Test
    public void part2() {
        assertEquals(6555315065024L, sumOfArrangements(input(this), 5));
    }

    private final Map<String, Long> cache = Maps.newHashMap();

    private long sumOfArrangements(Input input, int multiplier) {
        return input.lines().stream()
                .mapToLong(l -> {
                    cache.clear();
                    return countArrangements(pattern(l, multiplier), sequence(l, multiplier), 0, 0);
                })
                .sum();
    }

    private long countArrangements(String pattern, int[] sequence, int completeGroups, int currentGroup) {
        var key = pattern + Arrays.toString(sequence) + completeGroups + currentGroup;

        var result = cache.get(key);
        if (result != null) {
            return result;
        }

        result = countArrangementsImpl(pattern, sequence, completeGroups, currentGroup);
        cache.put(key, result);
        return result;
    }

    private long countArrangementsImpl(String pattern, int[] sequence, int completeGroups, int currentGroup) {
        if (pattern.isEmpty()) {
            if (completeGroups == sequence.length && currentGroup == 0) {
                return 1;
            } else if (completeGroups == sequence.length - 1 && sequence[sequence.length - 1] == currentGroup) {
                return 1;
            }
            return 0;
        }

        char next = pattern.charAt(0);
        if (next == '.') {
            if (currentGroup > 0) {
                if (sequence[completeGroups] == currentGroup) {
                    return countArrangements(pattern.substring(1), sequence, completeGroups + 1, 0);
                }
                return 0;
            }
            return countArrangements(pattern.substring(1), sequence, completeGroups, currentGroup);
        } else if (next == '#') {
            if (completeGroups < sequence.length && sequence[completeGroups] > currentGroup) {
                return countArrangements(pattern.substring(1), sequence, completeGroups, currentGroup + 1);
            } else {
                return 0;
            }
        } else { // '?' Try both options
            return countArrangements("#" + pattern.substring(1), sequence, completeGroups, currentGroup)
                    + countArrangements("." + pattern.substring(1), sequence, completeGroups, currentGroup);
        }
    }

    private String pattern(final String input, int multiplier) {
        return IntStream.range(0, multiplier)
                .mapToObj(i -> input.split(" ")[0])
                .collect(Collectors.joining("?"));
    }

    private int[] sequence(final String input, int multiplier) {
        return IntStream.range(0, multiplier)
                .mapToObj(i -> Utils.parseInts(input.split(" ")[1]))
                .flatMap(Collection::stream)
                .mapToInt(Integer::intValue)
                .toArray();
    }
}
