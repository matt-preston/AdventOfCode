package aoc.y2019.intcode;

import utils.Input;

import java.util.Arrays;

public interface Memory {

    static Memory init(Input input) {
        final var array = Arrays.stream(input.text().split(","))
                .map(String::trim)
                .mapToLong(Long::parseLong)
                .toArray();
        return new MemoryImpl(array);
    }

    long read(long address);

    void write(long address, long value);

    Memory copy();
}
