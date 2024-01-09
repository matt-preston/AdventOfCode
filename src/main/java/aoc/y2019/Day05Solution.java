package aoc.y2019;

import aoc.y2019.intcode.Computer;
import aoc.y2019.intcode.IO;
import aoc.y2019.intcode.Memory;
import aoc.y2019.intcode.Opcode;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2019, day = 5, name = "Sunny with a Chance of Asteroids")
public class Day05Solution {

    @Test
    public void testParameterModeParsing() {
        final var opcode = Opcode.decode(1002);
        assertEquals(2, opcode.opcode());
        assertEquals(0, opcode.nextParameterMode());
        assertEquals(1, opcode.nextParameterMode());
        assertEquals(0, opcode.nextParameterMode());
    }

    @Test
    public void testInputOutputOpcodes() {
        final var input = mockInput("3,0,4,0,99");
        assertOutput(0, input, 0);
        assertOutput(7, input, 7);
        assertOutput(-1, input, -1);
    }

    @Test
    public void testEquals8WithPositionMode() {
        final var input = mockInput("3,9,8,9,10,9,4,9,99,-1,8");
        assertOutput(1, input, 8);
        assertOutput(0, input, 7);
        assertOutput(0, input, 9);
    }

    @Test
    public void testEquals8WithImmediateMode() {
        final var input = mockInput("3,3,1108,-1,8,3,4,3,99");
        assertOutput(1, input, 8);
        assertOutput(0, input, 7);
        assertOutput(0, input, 9);
    }

    @Test
    public void testLessThan8WithPositionMode() {
        final var input = mockInput("3,9,7,9,10,9,4,9,99,-1,8");
        assertOutput(0, input, 9);
        assertOutput(0, input, 8);
        assertOutput(1, input, 7);
    }

    @Test
    public void testLessThan8WithImmediateMode() {
        final var input = mockInput("3,3,1107,-1,8,3,4,3,99");
        assertOutput(0, input, 9);
        assertOutput(0, input, 8);
        assertOutput(1, input, 7);
    }

    @Test
    public void testJumpIf0WithPositionMode() {
        final var input = mockInput("3,12,6,12,15,1,13,14,13,4,13,99,-1,0,1,9");
        assertOutput(0, input, 0);
        assertOutput(1, input, 1);
        assertOutput(1, input, 2);
    }

    @Test
    public void testJumpIf0WithImmediateMode() {
        final var input = mockInput("3,3,1105,-1,9,1101,0,0,12,4,12,99,1");
        assertOutput(0, input, 0);
        assertOutput(1, input, 1);
        assertOutput(1, input, 2);
    }

    @Test
    public void part1() {
        Memory memory = Memory.init(input(this));
        final var computer = new Computer(memory, new IO(ImmutableList.of(1)));
        computer.runToCompletion();

        final var output = computer.io().output();

        assertTrue(output.size() > 1);
        for (int i = 0; i < output.size() - 1; i++) {
            assertEquals(0, output.get(i));
        }
        assertEquals(9938601, output.get(output.size() - 1));
    }

    @Test
    public void part2WithMockData() {
        final var input = mockInput("""
                3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
                1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
                999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99
                """);
        assertOutput(999, input, 7);
        assertOutput(1000, input, 8);
        assertOutput(1001, input, 9);
    }

    @Test
    public void part2() {
        assertOutput(4283952, input(this), 5);
    }

    private void assertOutput(int expectedOutput, Input program, int input) {
        var computer = new Computer(Memory.init(program), new IO(ImmutableList.of(input)));
        computer.runToCompletion();
        assertEquals(List.of(expectedOutput), computer.io().output());
    }
}

