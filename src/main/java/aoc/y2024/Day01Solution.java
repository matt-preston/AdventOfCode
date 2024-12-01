package aoc.y2024;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 1, name = "Historian Hysteria")
public class Day01Solution {

    private static final String MOCK = """
            3   4
            4   3
            2   5
            1   3
            3   9
            3   3
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(11, difference(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(2196996, difference(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(31, similarity(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(23655822, similarity(input(this)));
    }

    private int difference(Input input) {
        var list1 = Lists.<Integer>newArrayList();
        var list2 = Lists.<Integer>newArrayList();

        for (String line : input.lines()) {
            var bits = line.split("\\s+", 2);

            list1.add(Integer.parseInt(bits[0]));
            list2.add(Integer.parseInt(bits[1]));
        }

        list1.sort(Comparator.naturalOrder());
        list2.sort(Comparator.naturalOrder());

        var difference = 0;
        for (int i = 0; i < list1.size(); i++) {
            difference += Math.abs(list1.get(i) - list2.get(i));
        }
        return difference;
    }


    private int similarity(Input input) {
        var list = Lists.<Integer>newArrayList();
        var bag = HashMultiset.<Integer>create();

        for (String line : input.lines()) {
            var bits = line.split("\\s+", 2);

            list.add(Integer.parseInt(bits[0]));
            bag.add(Integer.parseInt(bits[1]));
        }

        return list.stream()
                .mapToInt(value -> value * bag.count(value))
                .sum();
    }
}
