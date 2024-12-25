package aoc.y2024;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Section;
import utils.Utils;

import java.util.List;

import static com.google.common.collect.Streams.zip;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 25, name = "Code Chronicle")
public class Day25Solution {

    private static final String MOCK = """
            #####
            .####
            .####
            .####
            .#.#.
            .#...
            .....
            
            #####
            ##.##
            .#.##
            ...##
            ...#.
            ...#.
            .....
            
            .....
            #....
            #....
            #...#
            #.#.#
            #.###
            #####
            
            .....
            .....
            #.#..
            ###..
            ###.#
            ###.#
            #####
            
            .....
            .....
            .....
            #....
            #.#..
            #.#.#
            #####
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(3, fittingKeys(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(3508, fittingKeys(input(this)));
    }

    private int fittingKeys(Input input) {
        var locks = Lists.<List<Integer>>newArrayList();
        var keys = Lists.<List<Integer>>newArrayList();

        for (Section section : input.sections()) {
            var grid = Utils.matrix(section.text());
            if (grid[0][0] == '#') {
                locks.add(sumColumns(grid));
            } else {
                keys.add(sumColumns(grid));
            }
        }

        var matches = 0;
        for (List<Integer> k : keys) {
            for (List<Integer> l : locks) {
                if (!zip(k.stream(), l.stream(), Integer::sum).anyMatch(i -> i > 5)) {
                    matches++;
                }
            }
        }

        return matches;
    }

    private List<Integer> sumColumns(char[][] grid) {
        var result = Lists.<Integer>newArrayList();
        for (int x = 0; x < grid[0].length; x++) {
            var count = -1;
            for (char[] chars : grid) {
                if (chars[x] == '#') {
                    count++;
                }
            }
            result.add(count);
        }
        return result;
    }
}
