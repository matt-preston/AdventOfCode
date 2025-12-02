package aoc.y2020;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import utils.AdventOfCode;
import org.junit.jupiter.api.Test;
import utils.Input;
import utils.Section;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BinaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2020, day = 6, name = "Custom Customs")
public class Day06Solution {

    private static final String MOCK = """
            abc
            
            a
            b
            c
        
            ab
            ac
        
            a
            a
            a
            a
        
            b
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(11, solve(mockInput(MOCK), Sets::union));
    }

    @Test
    public void part1() {
        assertEquals(6911, solve(input(this), Sets::union));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(6, solve(mockInput(MOCK), Sets::intersection));
    }

    @Test
    public void part2() {
        assertEquals(3473, solve(input(this), Sets::intersection));
    }

    private int solve(Input input, BinaryOperator<Set<String>> accumulator) {
        return input.sections().stream()
                .mapToInt(section -> size(section.text(), accumulator))
                .sum();
    }

    private int size(String answers, BinaryOperator<Set<String>> accumulator) {
        return Arrays.stream(answers.split("\\n"))
                .map(s -> s.split(""))
                .map(s -> (Set<String>) ImmutableSet.copyOf(s))
                .reduce(accumulator)
                .orElseThrow()
                .size();
    }
}
