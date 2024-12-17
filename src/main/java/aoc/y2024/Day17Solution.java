package aoc.y2024;

import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2024, day = 17, name = "Chronospatial Computer")
public class Day17Solution {

    private static final String MOCK = """
            Register A: 729
            Register B: 0
            Register C: 0
            
            Program: 0,1,5,4,3,0
            """;

    private static final String MOCK2 = """
            Register A: 2024
            Register B: 0
            Register C: 0
            
            Program: 0,3,5,4,3,0
            """;

    @Test
    public void part1WithMockData() {
        assertEquals("4,6,3,5,6,3,5,2,1,0", executeProgram(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals("1,6,7,4,3,0,5,0,6", executeProgram(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(117440, findA(mockInput(MOCK2)));
    }

    @Test
    public void part2() {
        assertEquals(216148338630253L, findA(input(this)));
    }

    private String executeProgram(Input input) {
        var registers = Utils.parseInts(input.section(0).text().replaceAll("\\D", " "));
        var program = Utils.parseInts(input.section(1).text().substring(8)).stream()
                .mapToInt(Integer::intValue)
                .toArray();

        return executeProgram(program, registers.get(0), registers.get(1), registers.get(2)).stream().
                map(i -> Integer.toString(i)).
                collect(Collectors.joining(","));
    }

    private List<Integer> executeProgram(int[] program, long a, long b, long c) {
        return new Day17Computer(program, a, b, c)
                .run()
                .getOutputBuffer();
    }

    // Patterns repeating around factors of 2^3 (8).  Needed a lot of hints from the community, was lost with this one
    private long findA(Input input) {
        var registers = Utils.parseInts(input.section(0).text().replaceAll("\\D", " "));
        var programString = input.section(1).text().substring(8).trim();

        var programList = Utils.parseInts(programString).stream().toList();
        var program = programList.stream().mapToInt(Integer::intValue).toArray();

        for (long a = 0; a < (long) Math.pow(8, program.length); a++) {
            var output = executeProgram(program, a, registers.get(1), registers.get(2));

            if (programList.subList(program.length - output.size(), program.length).equals(output)) {
                if (output.size() == program.length) {
                    return a;
                }
                a = (Math.max(a, 1) * 8) - 1;
            }
        }

        return -1;
    }
}
