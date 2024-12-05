package aoc.y2024;

import com.google.common.collect.*;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.Arrays;
import java.util.List;

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
        var rules = rules(input);
        var updates = updates(input);
        var sum = 0;

        for (List<Integer> update : updates) {
            if(valid(rules, update)) {
                sum += update.get((update.size() - 1) / 2);
            }
        }

        return sum;
    }

    private int sumCorrectedMiddlePages(Input input) {
        var rules = rules(input);
        var updates = updates(input);
        var sum = 0;

        for (List<Integer> update : updates) {
            if(!valid(rules, update)) {
                var inverted = Multimaps.invertFrom(rules, HashMultimap.create());

                var copyOfUpdate = Sets.newLinkedHashSet(update);
                var found = -1;

                for (int i = 0; i < (update.size() + 1) / 2; i++) {
                    var iterator = copyOfUpdate.iterator();
                    while(iterator.hasNext()) {
                        var page = iterator.next();
                        if(intersection(inverted.get(page), copyOfUpdate).isEmpty()) {
                            found = page;
                            inverted.removeAll(page);
                            inverted.values().remove(page);
                            iterator.remove();
                            break;
                        }
                    }
                }
                sum += found;
            }
        }

        return sum;
    }


    private boolean valid(Multimap<Integer, Integer> rules, List<Integer> update) {
        var seen = Sets.<Integer>newHashSet();
        for (Integer page : update) {
            for (Integer p : rules.get(page)) {
                if (seen.contains(p)) {
                    return false;
                }
            }
            seen.add(page);
        }
        return true;
    }

    private Multimap<Integer, Integer> rules(Input input) {
        var s = input.text().split("\n\n")[0].trim();
        var result = HashMultimap.<Integer, Integer>create();

        for (String line : s.split("\n")) {
            var bits = line.split("\\|", 2);
            result.put(Integer.parseInt(bits[0]), Integer.parseInt(bits[1]));
        }

        return result;
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
