package aoc.y2024;

import com.google.common.collect.Lists;

import java.util.List;

public class Day17Computer {

    private long a;
    private long b;
    private long c;

    private int ip;
    private int[] program;

    private boolean halt;

    private List<Integer> outputBuffer = Lists.newArrayList();

    public Day17Computer(int[] program, long a, long b, long c) {
        this.program = program;
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Day17Computer run() {
        while(!halt) {
            step();
        }
        return this;
    }

    public List<Integer> getOutputBuffer() {
        return outputBuffer;
    }

    private void step() {
        if (ip >= program.length) {
            halt = true;
            return;
        }

        var opcode = program[ip++];
        var operand = program[ip++];

        switch(opcode) {
            case 0 -> adv(operand);
            case 1 -> bxl(operand);
            case 2 -> bst(operand);
            case 3 -> jnz(operand);
            case 4 -> bxc();
            case 5 -> out(operand);
            case 6 -> bdv(operand);
            case 7 -> cdv(operand);
        }
    }

    private void adv(int operand) {
        a = (long) (a / Math.pow(2, comboOperand(operand)));
    }

    private void bxl(int operand) {
        b ^= operand;
    }

    private void bst(int operand) {
        b = comboOperand(operand) & 0x7;
    }

    private void jnz(int operand) {
        if (a != 0) {
            ip = operand;
        }
    }

    private void bxc() {
        b ^= c;
    }

    private void out(int operand) {
        outputBuffer.add((int) (comboOperand(operand) & 0x7));
    }

    private void bdv(int operand) {
        b = (long) (a / Math.pow(2, comboOperand(operand)));
    }

    private void cdv(int operand) {
        c = (long) (a / Math.pow(2, comboOperand(operand)));
    }

    private long comboOperand(int value) {
        return switch(value) {
            case int _ when value < 4 -> value;
            case 4 -> a;
            case 5 -> b;
            case 6 -> c;
            default -> throw new IllegalStateException("Can't handle combo  operand: " + value);
        };
    }
}
