package aoc.y2019.intcode;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Queue;

public class IO {

    private final Queue<Integer> input;
    private final List<Integer> output;

    public IO(List<Integer> input) {
        this.input = Lists.newLinkedList(input);
        this.output = Lists.newArrayList();
    }

    public int input() {
        final var poll = input.poll();
        if (poll != null) {
            return poll;
        } else {
            throw new NullPointerException();
        }
    }

    public void output(int value) {
        this.output.add(value);
    }

    public List<Integer> output() {
        return this.output;
    }
}
