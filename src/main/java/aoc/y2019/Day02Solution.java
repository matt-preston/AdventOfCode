package aoc.y2019;

import aoc.y2019.intcode.Computer;
import aoc.y2019.intcode.Memory;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2019, day = 2, name = "1202 Program Alarm")
public class Day02Solution {

    @Test
    public void part1WithMockData() {
        Memory memory = Memory.init(mockInput("1,9,10,3,2,3,11,0,99,30,40,50"));
        final var computer = new Computer(memory);
        computer.runToCompletion();

        assertEquals(3500, memory.read(0));
    }

    @Test
    public void part1() {
        Memory memory = Memory.init(input(this));
        assertEquals(4330636, executeWithReplacements(memory, 12, 2));
    }

    @Test
    public void part2() {
        final var memory = Memory.init(input(this));
        for (int noun = 0; noun < 100; noun++) {
            for (int verb = 0; verb < 100; verb++) {
                var result = executeWithReplacements(memory.copy(), noun, verb);
                if (result == 19690720) {
                    assertEquals(6086, (100 * noun) + verb);
                    return;
                }
            }
        }
        fail("Did not find the right values");
    }

    private int executeWithReplacements(Memory memory, int noun, int verb) {
        memory.write(1, noun);
        memory.write(2, verb);

        var computer = new Computer(memory);
        computer.runToCompletion();

        return memory.read(0);
    }
}
