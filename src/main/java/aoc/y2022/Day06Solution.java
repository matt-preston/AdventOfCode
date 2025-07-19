package aoc.y2022;

import utils.AdventOfCode;
import org.junit.jupiter.api.Test;
import utils.Input;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2022, day = 6, name = "Tuning Trouble")
public class Day06Solution {

    private static final String MOCK1 = "mjqjpqmgbljsphdztnvjfqwrcgsmlb";
    private static final String MOCK2 = "bvwbjplbgvbhsrlpgdmjqwftvncz";
    private static final String MOCK3 = "nppdvjthqldpwncqszvftbrmjlhg";
    private static final String MOCK4 = "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg";
    private static final String MOCK5 = "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw";

    @Test
    public void part1WithMockData() {
        assertEquals(7, solve(mockInput(MOCK1), 4));
        assertEquals(5, solve(mockInput(MOCK2), 4));
        assertEquals(6, solve(mockInput(MOCK3), 4));
        assertEquals(10, solve(mockInput(MOCK4), 4));
        assertEquals(11, solve(mockInput(MOCK5), 4));
    }

    @Test
    public void part1() {
        assertEquals(1042, solve(input(this), 4));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(19, solve(mockInput(MOCK1), 14));
        assertEquals(23, solve(mockInput(MOCK2), 14));
        assertEquals(23, solve(mockInput(MOCK3), 14));
        assertEquals(29, solve(mockInput(MOCK4), 14));
        assertEquals(26, solve(mockInput(MOCK5), 14));
    }

    @Test
    public void part2() {
        assertEquals(2980, solve(input(this), 14));
    }

    private int solve(Input input, int length) {
        var buffer = new Character[length];
        var index = 0;

        var c = input.text().toCharArray();
        for (int i = 0; i < c.length; i++) {
           buffer[index] = c[i];

           if (i >= buffer.length - 1) {
               var set = new HashSet<>(Arrays.asList(buffer));
               if (set.size() == buffer.length) {
                   return i + 1;
               }
           }

           index = (index + 1) % buffer.length;
        }

        return 0;
    }
}
