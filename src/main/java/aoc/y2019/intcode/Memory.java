package aoc.y2019.intcode;

import utils.Input;

import java.util.Arrays;

public interface Memory {

    static Memory init(Input input) {
        final var array = Arrays.stream(input.text().split(","))
                .map(String::trim)
                .mapToInt(Integer::parseInt)
                .toArray();
        return new MemoryImpl(array);
    }


    int read(int address);

    void write(int address, int value);

    Memory copy();
}
