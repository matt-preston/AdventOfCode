package aoc.y2020;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import utils.AdventOfCode;
import org.junit.jupiter.api.Test;
import utils.Input;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2020, day = 7, name = "Handy Haversacks")
public class Day07Solution {

    record Rule(int count, String bag){}

    private static final String MOCK = """
            light red bags contain 1 bright white bag, 2 muted yellow bags.
            dark orange bags contain 3 bright white bags, 4 muted yellow bags.
            bright white bags contain 1 shiny gold bag.
            muted yellow bags contain 2 shiny gold bags, 9 faded blue bags.
            shiny gold bags contain 1 dark olive bag, 2 vibrant plum bags.
            dark olive bags contain 3 faded blue bags, 4 dotted black bags.
            vibrant plum bags contain 5 faded blue bags, 6 dotted black bags.
            faded blue bags contain no other bags.
            dotted black bags contain no other bags.
            """;

    private static final String MOCK2 = """
            shiny gold bags contain 2 dark red bags.
            dark red bags contain 2 dark orange bags.
            dark orange bags contain 2 dark yellow bags.
            dark yellow bags contain 2 dark green bags.
            dark green bags contain 2 dark blue bags.
            dark blue bags contain 2 dark violet bags.
            dark violet bags contain no other bags.
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(4, bagsOutside(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(164, bagsOutside(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(126, bagsInside(mockInput(MOCK2)));
    }

    @Test
    public void part2() {
        assertEquals(7872, bagsInside(input(this)));
    }

    private int bagsOutside(Input input) {
        var graph = HashMultimap.<String, String>create();
        graph(input).forEach((s, rule) -> graph.put(rule.bag, s)); // invert the graph

        var visit = Sets.<String>newTreeSet();
        visit.add("shiny gold");

        var seen = Sets.<String>newHashSet();

        while(!visit.isEmpty()) {
            var next = visit.removeFirst();
            var containedIn = graph.get(next);

            for (String bag : containedIn) {
                if (seen.add(bag)) {
                    visit.add(bag);
                }
            }
        }

        return seen.size();
    }

    private int bagsInside(Input input) {
        return countInside(graph(input), "shiny gold");
    }

    private int countInside(Multimap<String, Rule> graph, String bag) {
        var count = 0;
        for (Rule rule : graph.get(bag)) {
            count += (rule.count * (1 + countInside(graph, rule.bag)));
        }
        return count;
    }

    private static HashMultimap<String, Rule> graph(Input input) {
        var graph = HashMultimap.<String, Rule>create();

        for (String line : input.lines()) {
            var bits = line.split("\\s+bags contain\\s+");
            var contents = bits[1].split("[,.]");
            for (String content : contents) {
                var p = Pattern.compile("(\\d) (.*) bag(s)?");
                var m = p.matcher(content.trim());
                if(m.matches()) {
                    graph.put(bits[0], new Rule(Integer.parseInt(m.group(1)), m.group(2)));
                }
            }
        }
        return graph;
    }
}
