package aoc.y2019;

import aoc.y2019.intcode.Computer;
import aoc.y2019.intcode.IO;
import aoc.y2019.intcode.Memory;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AdventOfCode(year = 2019, day = 9, name = "Sensor Boost")
public class Day09Solution {

    @Test
    public void part1WithSampleData() {
        assertOutput(List.of(109L,1L,204L,-1L,1001L,100L,1L,100L,1008L,100L,16L,101L,1006L,101L,0L,99L), Input.mockInput("109,1,204,-1,1001,100,1,100,1008,100,16,101,1006,101,0,99"));
        assertOutput(List.of(1219070632396864L), Input.mockInput("1102,34915192,34915192,7,4,7,99,0"));
        assertOutput(List.of(1125899906842624L), Input.mockInput("104,1125899906842624,99"));
    }

    @Test
    public void part1() {
        assertOutput(List.of(2171728567L), Input.input(this), 1L);
    }

    @Test
    public void part2() {
        assertOutput(List.of(49815L), Input.input(this), 2L);
    }

    private void assertOutput(List<Long> expectedOutput, Input program, long input) {
        var computer = new Computer(Memory.init(program), new IO(ImmutableList.of(input)));
        computer.runToCompletion();
        assertEquals(expectedOutput, computer.io().output());
    }

    private void assertOutput(List<Long> expectedOutput, Input program) {
        var computer = new Computer(Memory.init(program), new IO(ImmutableList.of()));
        computer.runToCompletion();
        assertEquals(expectedOutput, computer.io().output());
    }
}
