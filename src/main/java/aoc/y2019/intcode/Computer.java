package aoc.y2019.intcode;

import com.google.common.collect.Lists;

public class Computer {

    private final Memory memory;
    private final IO io;
    private long pc = 0;
    private boolean running;
    private long relativeBase;

    public Computer(Memory memory) {
      this(memory, new IO(Lists.newLinkedList()));
    }

    public Computer(Memory memory, IO io) {
        this.memory = memory;
        this.io = io;
        this.running = true;
        this.relativeBase = 0;
    }

    public Memory memory() {
        return memory;
    }

    public IO io() {
        return io;
    }

    public boolean running() {
        return running;
    }

    public void runToCompletion() {
        while(running()) {
            step();
        }
    }

    public boolean runUntilInputRequired() {
        while(running() && (io.hasInputQueued() || !requireInput())) {
            step();
        }
        return running();
    }

    public boolean runUntilOutputAvailable() {
        while(running() && !io().hasOutput()) {
            step();
        }

        return running();
    }

    public void step() {
        var opcode = Opcode.decode(memory.read(pc++));

        switch (opcode.opcode()) {
            case 1 -> {  // add
                var p1 = inputParameter(opcode.nextParameterMode());
                var p2 = inputParameter(opcode.nextParameterMode());
                var p3 = outputParameter(opcode.nextParameterMode());
                memory.write(p3, p1 + p2);
            }
            case 2 -> { // multiply
                var p1 = inputParameter(opcode.nextParameterMode());
                var p2 = inputParameter(opcode.nextParameterMode());
                var p3 = outputParameter(opcode.nextParameterMode());
                memory.write(p3, p1 * p2);
            }
            case 3 -> { // read from input
                var p1 = outputParameter(opcode.nextParameterMode());
                memory.write(p1, io.input());
            }
            case 4 -> { // write to output
                var p1 = inputParameter(opcode.nextParameterMode());
                io.output(p1);
            }
            case 5 -> { // jump if true
                var p1 = inputParameter(opcode.nextParameterMode());
                var p2 = inputParameter(opcode.nextParameterMode());
                if (p1 != 0) {
                    pc = p2;
                }
            }
            case 6 -> { // jump if false
                var p1 = inputParameter(opcode.nextParameterMode());
                var p2 = inputParameter(opcode.nextParameterMode());
                if (p1 == 0) {
                    pc = p2;
                }
            }
            case 7 -> { // less than
                var p1 = inputParameter(opcode.nextParameterMode());
                var p2 = inputParameter(opcode.nextParameterMode());
                var p3 = outputParameter(opcode.nextParameterMode());
                memory.write(p3, p1 < p2 ? 1 : 0);
            }
            case 8 -> { // equals
                var p1 = inputParameter(opcode.nextParameterMode());
                var p2 = inputParameter(opcode.nextParameterMode());
                var p3 = outputParameter(opcode.nextParameterMode());
                memory.write(p3, p1 == p2 ? 1 : 0);
            }
            case 9 -> { // adjust relative base
                var p1 = inputParameter(opcode.nextParameterMode());
                relativeBase += p1;
            }
            case 99 -> running = false;
            default -> throw new IllegalStateException("Unknown opcode: " + opcode.opcode());
        }
    }

    private boolean requireInput() {
        return Opcode.decode(memory.read(pc)).opcode() == 3;
    }

    private long inputParameter(int parameterMode) {
        return switch (parameterMode) {
            case 0 -> memory.read(memory.read(pc++));
            case 1 -> memory.read(pc++);
            case 2 -> memory.read(relativeBase + memory.read(pc++));
            default -> throw unexpectedParameterMode(parameterMode);
        };
    }

    private long outputParameter(int parameterMode) {
        return switch (parameterMode) {
            case 0 -> memory.read(pc++);
            case 2 -> relativeBase + memory.read(pc++);
            default -> throw unexpectedParameterMode(parameterMode);
        };
    }

    private IllegalStateException unexpectedParameterMode(int parameterMode) {
        return new IllegalStateException("Unexpected parameter mode: [" + parameterMode + "]");
    }
}
