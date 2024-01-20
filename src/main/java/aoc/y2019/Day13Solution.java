package aoc.y2019;

import aoc.y2019.intcode.Computer;
import aoc.y2019.intcode.Memory;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;

@AdventOfCode(year = 2019, day = 13, name = "")
public class Day13Solution {

    private static final int WIDTH = 42;
    private static final int HEIGHT = 20;

    @Test
    public void part1() {
        var input = input(this);
        var computer = new Computer(Memory.init(input));

        computer.runToCompletion();

        var count = 0;
        final var output = computer.io().output();
        for (int i = 0; i < output.size(); i += 3) {
            if (output.get(i + 2) == 2) {
                count++;
            }
        }

        assertEquals(207, count);
    }

    @Test
    public void part2()  {
        assertEquals(10247, winningScore(true));
    }

    private int winningScore(boolean headless) {
        var input = input(this);
        var computer = new Computer(Memory.init(input));
        computer.memory().write(0, 2); // play for free

        char[] buffer = new char[WIDTH * HEIGHT];
        Arrays.fill(buffer, ' ');

        int score = 0;

        int paddleX = 0;
        int ballX = 0;

        while (computer.running()) {
            computer.runUntilInputRequired();

            final var output = computer.io().output();
            for (int i = 0; i < output.size(); i += 3) {
                final var x = output.get(i).intValue();
                final var y = output.get(i + 1).intValue();
                final var value = output.get(i + 2).intValue();
                if (x < 0) {
                    score = value;
                } else {
                    buffer[(y * WIDTH) + x] = switch (value) {
                        case 1 -> '#'; // wall
                        case 2 -> '%'; // block
                        case 3 -> '='; // paddle
                        case 4 -> '@'; // ball
                        default -> ' ';
                    };

                    if (value == 3) {
                        paddleX = x;
                    } else if (value == 4) {
                        ballX = x;
                    }

                }
            }
            output.clear();

            if (!headless) {
                render(buffer, score);
            }

            if (ballX > paddleX) {
                computer.io().queueInput(1);
            } else if (ballX < paddleX) {
                computer.io().queueInput(-1);
            } else {
                computer.io().queueInput(0);
            }
        }

        return score;
    }

    private void render(char[] buffer, int score) {
        System.out.println("Score: " + score);
        for (int row = 0; row < HEIGHT; row++) {
            System.out.println(new String(buffer, row * WIDTH, WIDTH));
        }
        System.out.println();
    }

}
