package aoc.y2019.intcode;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Queue;

public class IO {

    private final Queue<Long> input;
    private final List<Long> output;

    public IO(List<Long> input) {
        this.input = Lists.newLinkedList(input);
        this.output = Lists.newArrayList();
    }

    public long input() {
        final var poll = input.poll();
        if (poll != null) {
            return poll;
        } else {
            throw new NullPointerException();
        }
    }

    void output(long value) {
        this.output.add(value);
    }

    public boolean hasOutput() {
        return !output.isEmpty();
    }

    public List<Long> output() {
        return this.output;
    }

    public long takeOutput() {
        return output.remove(0);
    }

    public void queueInput(long value) {
        this.input.add(value);
    }
}
