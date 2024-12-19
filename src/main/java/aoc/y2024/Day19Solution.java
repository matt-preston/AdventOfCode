package aoc.y2024;

import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 19, name = "Linen Layout")
public class Day19Solution {

    private static final String MOCK = """
            r, wr, b, g, bwu, rb, gb, br
            
            brwrr
            bggr
            gbbr
            rrbgbr
            ubwu
            bwurrg
            brgr
            bbrgwb
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(6, possibleDesigns(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(360, possibleDesigns(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(16, possibleArrangements(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(577474410989846L, possibleArrangements(input(this)));
    }

    private long possibleDesigns(Input input) {
        var available = input.section(0).text().split(",\\s*");
        var designs = input.section(1).lines();
        return designs.stream()
                .filter(d -> possibleArrangements(d, available) > 0)
                .count();
    }

    private long possibleArrangements(Input input) {
        var available = input.section(0).text().split(",\\s*");
        var designs = input.section(1).lines();
        return designs.stream()
                .mapToLong(d -> possibleArrangements(d, available))
                .sum();
    }

    private final Map<String, Long> cache = Maps.newHashMap();

    private long possibleArrangements(String design, String[] available) {
        var result = cache.get(design);
        if (result == null) {
            result = possibleArrangementsImpl(design, available);
            cache.put(design, result);
        }
        return result;
    }

    private long possibleArrangementsImpl(String design, String[] available) {
        if (design.isEmpty()) {
            return 1;
        }

        var count = 0L;
        for (String pattern : available) {
            if (design.startsWith(pattern)) {
                count += possibleArrangements(design.substring(pattern.length()), available);
            }
        }
        return count;
    }
}
