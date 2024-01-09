package aoc.y2019;

import aoc.y2019.intcode.Computer;
import aoc.y2019.intcode.IO;
import aoc.y2019.intcode.Memory;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.List;

import static com.google.common.collect.Collections2.permutations;
import static java.lang.Math.max;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2019, day = 7, name = "Amplification Circuit")
public class Day07Solution {


    @Test
    public void part1WithSampleData() {
        assertEquals(43210, maxAmplifierOutput(mockInput("3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0")));
        assertEquals(54321, maxAmplifierOutput(mockInput("""
                3,23,3,24,1002,24,10,24,1002,23,-1,23,
                101,5,23,23,1,24,23,23,4,23,99,0,0
                """)));
        assertEquals(65210, maxAmplifierOutput(mockInput("""
                3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,
                1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0
                """)));
    }

    @Test
    public void part1() {
        assertEquals(255840, maxAmplifierOutput(input(this)));
    }

    @Test
    public void part2WithSampleData() {
        assertEquals(139629729, maxAmplifierOutputWithFeedback(mockInput("""
                3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,
                27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5
                """)));

        assertEquals(18216, maxAmplifierOutputWithFeedback(mockInput("""
                3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,
                -5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,
                53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10
                """)));
    }

    @Test
    public void part2() {
        assertEquals(84088865, maxAmplifierOutputWithFeedback(input(this)));
    }

    private int maxAmplifierOutput(Input input) {
        var memory = Memory.init(input);
        var maxOutput = 0;
        for (List<Integer> permutation : permutations(List.of(0, 1, 2, 3, 4))) {
            var output = 0;
            for (Integer phase : permutation) {
                final var amplifier = new Computer(memory.copy(), new IO(ImmutableList.of(phase, output)));
                amplifier.runToCompletion();
                output = amplifier.io().output().get(0);
            }
            maxOutput = max(maxOutput, output);
        }
        return maxOutput;
    }

    private int maxAmplifierOutputWithFeedback(Input input) {
        var memory = Memory.init(input);
        var maxOutput = 0;
        for (List<Integer> permutation : permutations(List.of(5, 6, 7, 8, 9))) {
            final var amplifiers = permutation.stream()
                    .map(p -> new Computer(memory.copy(), new IO(ImmutableList.of(p))))
                    .toList();

            var running = true;
            var output = 0;
            while (running) {
                for (Computer amplifier : amplifiers) {
                    amplifier.io().queueInput(output);
                    while (amplifier.io().hasOutput() && amplifier.running()) {
                        amplifier.step();
                    }
                    if (!amplifier.running()) {
                        running = false;
                    } else {
                        output = amplifier.io().output().remove(0);
                    }
                }
            }
            maxOutput = max(maxOutput, output);
        }
        return maxOutput;
    }
}
