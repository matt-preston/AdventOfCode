package aoc.y2024;

import com.google.common.collect.*;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.Comparators.isInOrder;
import static com.google.common.collect.Ordering.from;
import static com.google.common.collect.Sets.intersection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 5, name = "Print Queue")
public class Day05Solution {

    private static final String MOCK = """
            47|53
            97|13
            97|61
            97|47
            75|29
            61|13
            75|53
            29|13
            97|29
            53|29
            61|53
            97|53
            61|29
            47|13
            75|47
            97|75
            47|61
            75|61
            47|29
            75|13
            53|13
            
            75,47,61,53,29
            97,61,53,29,13
            75,29,13
            75,97,47,61,53
            61,13,29
            97,13,75,29,47
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(143, sumValidMiddlePages(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(5268, sumValidMiddlePages(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(123, sumCorrectedMiddlePages(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(5799, sumCorrectedMiddlePages(input(this)));
    }

    private int sumValidMiddlePages(Input input) {
        var comparator = comparator(input);
        return updates(input).stream()
                .filter(pages -> isInOrder(pages, comparator))
                .mapToInt(pages -> pages.get((pages.size() - 1) / 2))
                .sum();
    }

    private int sumCorrectedMiddlePages(Input input) {
        var comparator = comparator(input);
        return updates(input).stream()
                .filter(pages -> !isInOrder(pages, comparator))
                .map(pages -> from(comparator).sortedCopy(pages))
                .mapToInt(pages -> pages.get((pages.size() - 1) / 2))
                .sum();
    }

    private Comparator<Integer> comparator(Input input) {
        var rules = HashMultimap.<Integer, Integer>create();

        var text = input.text().split("\n\n")[0].trim();
        for (String line : text.split("\n")) {
            var bits = line.split("\\|", 2);
            rules.put(Integer.parseInt(bits[0]), Integer.parseInt(bits[1]));
        }

        return (o1, o2) -> {
            if (o1.equals(o2)) {
                return 0;
            } else if (rules.get(o1).contains(o2)) {
                return -1;
            } else {
                return 1;
            }
        };
    }

    private List<List<Integer>> updates(Input input) {
        var s = input.text().split("\n\n")[1].trim();
        var result = Lists.<List<Integer>>newArrayList();

        for (String line : s.split("\n")) {
            result.add(Arrays.stream(line.trim().split(",")).map(Integer::parseInt).toList());
        }

        return result;
    }
}
