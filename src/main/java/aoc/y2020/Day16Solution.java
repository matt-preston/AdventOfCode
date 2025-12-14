package aoc.y2020;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.skip;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2020, day = 16, name = "Ticket Translation")
public class Day16Solution {

    private static final String MOCK = """
            class: 1-3 or 5-7
            row: 6-11 or 33-44
            seat: 13-40 or 45-50
            
            your ticket:
            7,1,14
            
            nearby tickets:
            7,3,47
            40,4,50
            55,2,20
            38,6,12
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(71, solve(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(25916, solve(input(this)));
    }

    @Test
    public void part2() {
        assertEquals(2564529489989L, solve2(input(this)));
    }

    record Rule(int from, int to) {
        public boolean contains(int value) {
            return from <= value && to >= value;
        }
    }

    record Rules(String name, Rule r1, Rule r2) {
        public boolean contains(int value) {
            return r1.contains(value) || r2.contains(value);
        }
    }

    private int solve(Input input) {
        var rules = input.section(0).lines().stream()
                .map(this::rules)
                .toList();

        var result = 0;
        for (String line : skip(input.section(2).lines(), 1)) {
            for (Integer i : Utils.parseInts(line)) {
                if (rules.stream().noneMatch(r -> r.contains(i))) {
                    result += i;
                }
            }
        }

        return result;
    }

    private long solve2(Input input) {
        var rules = input.section(0).lines().stream()
                .map(this::rules)
                .toList();


        var valid = Streams.stream(skip(input.section(2).lines(), 1))
                .filter(l -> {
                    for (Integer i : Utils.parseInts(l)) {
                        if (rules.stream().noneMatch(r -> r.contains(i))) {
                            return false;
                        }
                    }
                    return true;
                })
                .toList();

        var possibilities = HashMultimap.<Integer, Rules>create();

        // find which next position can only map to which rules
        for (int position = 0; position < rules.size(); position++) {
            var unassigned = Lists.newArrayList(rules);

            for (String line : valid) {
                var value = Utils.parseInts(line).get(position);
                unassigned.removeIf(r -> !r.contains(value));
            }

            possibilities.putAll(position, unassigned);
        }

        var order = new Rules[rules.size()];

        while (!possibilities.isEmpty()) {
            Integer next = 0;
            for (Integer key : possibilities.keySet()) {
                if (possibilities.get(key).size() == 1) {
                    next = key;
                    break;
                }
            }
            var value = possibilities.removeAll(next).iterator().next();
            order[next] = value;
            for (Integer key : possibilities.keySet()) {
                possibilities.get(key).remove(value);
            }
        }

        var myTicket = Utils.parseInts(input.section(1).linesArray()[1]);

        var result = 1L;
        for (int i = 0; i < order.length; i++) {
            if (order[i].name().startsWith("departure")) {
                result *= (long) myTicket.get(i);
            }
        }

        return result;
    }

    private Rules rules(String line) {
        var pattern = Pattern.compile("([\\w\\s]+): (\\d+-\\d+) or (\\d+-\\d+)");
        var matcher = pattern.matcher(line);
        checkState(matcher.matches());
        return new Rules(matcher.group(1), rule(matcher.group(2)), rule(matcher.group(3)));
    }

    private Rule rule(String s) {
        var nums = Utils.parseInts(s, "-");
        return new Rule(nums.get(0), nums.get(1));
    }
}
