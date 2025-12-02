package aoc.y2020;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2020, day = 8, name = "Handheld Halting")
public class Day08Solution {

    enum Operation {
        NOP, ACC, JMP
    }

    record Instruction(Operation operation, int arg) {
    }

    private static final String MOCK = """
            nop +0
            acc +1
            jmp +4
            acc +3
            jmp -3
            acc -99
            acc +1
            jmp -4
            acc +6
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(5, solve(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(1930, solve(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(8, solvePart2(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(1688, solvePart2(input(this)));
    }

    private int solve(Input input) {
        return run(parse(input)).acc();
    }

    private int solvePart2(Input input) {
        var instructions = parse(input);

        var state = run(instructions);

        for (Integer i : state.executed()) {
            var original = instructions.get(i);
            if (original.operation != Operation.ACC) {
                var copy = Lists.newArrayList(instructions);

                var newOp = original.operation == Operation.NOP ? Operation.JMP : Operation.NOP;
                copy.set(i, new Instruction(newOp, original.arg));

                var newState = run(copy);
                if (!newState.loop()) {
                    return newState.acc();
                }
            }
        }

        return 0;
    }

    private record State(boolean loop, int acc, Set<Integer> executed){}

    private State run(List<Instruction> instructions) {
        int pc = 0;
        int acc = 0;
        var executed = Sets.<Integer>newHashSet();

        while (executed.add(pc)) {
            if (pc >= instructions.size()) {
                return new State(false, acc, executed); // running instruction after end
            }

            var instr = instructions.get(pc);
            pc += switch (instr.operation) {
                case ACC -> {
                    acc += instr.arg;
                    yield 1;
                }
                case JMP -> instr.arg;
                case NOP -> 1;
            };
        }

        return new State(true, acc, executed); // hit infinite loop
    }

    private List<Instruction> parse(Input input) {
        return input.lines().stream()
                .map(this::op)
                .toList();
    }

    private Instruction op(String line) {
        var p = Pattern.compile("(\\w+) ([+-])(\\d+)");
        var m = p.matcher(line);
        if (!m.matches()) {
            throw new IllegalStateException();
        }
        var op = switch (m.group(1)) {
            case "acc" -> Operation.ACC;
            case "jmp" -> Operation.JMP;
            default -> Operation.NOP;
        };

        int arg = Integer.parseInt(m.group(3));
        if (m.group(2).equals("-")) {
            arg *= -1;
        }

        return new Instruction(op, arg);
    }
}
