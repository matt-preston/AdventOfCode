package aoc.y2019.intcode;

import java.util.Arrays;

class MemoryImpl implements Memory {

    private final int[] memory;

    MemoryImpl(int[] memory) {
        this.memory = memory;
    }

    @Override
    public int read(int address) {
        return memory[address];
    }

    @Override
    public void write(int address, int value) {
        memory[address] = value;
    }

    @Override
    public Memory copy() {
        return new MemoryImpl(Arrays.copyOf(memory, memory.length));
    }
}
