package aoc.y2019.intcode;

import static com.google.common.base.Preconditions.checkState;

class MemoryImpl implements Memory {

    private final long[] memory;

    MemoryImpl(long[] memory) {
        this.memory = new long[1100];
        System.arraycopy(memory, 0, this.memory, 0, memory.length);
    }

    @Override
    public long read(long address) {
        checkState(address >= 0, "Attempting to use a negative address");
        checkState(address < Integer.MAX_VALUE, "Attempting to address beyond the range of an int");
        return memory[(int) address];
    }

    @Override
    public void write(long address, long value) {
        checkState(address >= 0, "Attempting to use a negative address");
        checkState(address < Integer.MAX_VALUE, "Attempting to address beyond the range of an int");
        memory[(int)address] = value;
    }

    @Override
    public Memory copy() {
        return new MemoryImpl(memory);
    }
}
