package aoc.y2025;

import utils.AdventOfCode;
import org.junit.jupiter.api.Test;
import utils.Input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;
import static utils.Input.mockInput;

@AdventOfCode(year = 2025, day = 1, name = "Secret Entrance")
public class Day01Solution {

    private static final String MOCK = """
            L68
            L30
            R48
            L5
            R60
            L55
            L1
            L99
            R14
            L82
            """;

    @Test
    public void part1WithMockData() {
        assertEquals(3, pointsAtZero(mockInput(MOCK)));
    }

    @Test
    public void part1() {
        assertEquals(1195, pointsAtZero(input(this)));
    }

    @Test
    public void part2WithMockData() {
        assertEquals(6, passesZero(mockInput(MOCK)));
    }

    @Test
    public void part2() {
        assertEquals(6770, passesZero(input(this)));
    }

    private int pointsAtZero(Input input) {
        var dial = 50;
        var count = 0;

        for (String line : input.lines()) {
            var turn = line.startsWith("R") ? 1 : -1;
            turn *= Integer.parseInt(line.substring(1));
            dial = (dial + turn) % 100;
            if (dial == 0) {
                count++;
            }
        }

        return count;
    }

    class Lock {
        private int dial = 50;
        private int count;

        private void right(int times) {
            for (int i = 0; i < times; i++) {
                right();
            }
        }

        private void left(int times) {
            for (int i = 0; i < times; i++) {
                left();
            }
        }

        public void right() {
            dial = (dial + 1) % 100;
            if (dial == 0) {
                count++;
            }
        }

        public void left() {
            dial -= 1;
            if (dial < 0) {
                dial = 99;
            }

            if (dial == 0) {
                count++;
            }
        }
    }

    private int passesZero(Input input) {
        var lock = new Lock();

        for (String line : input.lines()) {
            var times = Integer.parseInt(line.substring(1));
            if (line.charAt(0) == 'R') {
                lock.right(times);
            } else {
                lock.left(times);
            }
        }

        return lock.count;
    }
}

