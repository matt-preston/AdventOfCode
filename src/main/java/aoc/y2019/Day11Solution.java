package aoc.y2019;

import aoc.y2019.intcode.Computer;
import aoc.y2019.intcode.Memory;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import utils.AdventOfCode;
import utils.Input;
import utils.Vector2;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.Input.input;

@AdventOfCode(year = 2019, day = 11, name = "Space Police")
public class Day11Solution {

    enum Direction {
        UP, DOWN, LEFT, RIGHT;

        Direction rotateRight() {
            return switch (this) {
                case UP -> RIGHT;
                case RIGHT -> DOWN;
                case DOWN -> LEFT;
                case LEFT -> UP;
            };
        }

        Direction rotateLeft() {
            return rotateRight().rotateRight().rotateRight();
        }

        Vector2 move(Vector2 position) {
            Vector2 vector = switch (this) {
                case UP -> new Vector2(0, -1);
                case DOWN -> new Vector2(0, 1);
                case RIGHT -> new Vector2(1, 0);
                case LEFT -> new Vector2(-1, 0);
            };
            return position.translate(vector);
        }
    }

    enum Colour {
        BLACK, WHITE
    }

    @Test
    public void part1() {
        assertEquals(1681, runPrintingRobot(input(this), Colour.BLACK).size());
    }

    @Test
    public void part2() {
        final var vectorMap = runPrintingRobot(input(this), Colour.WHITE);

        final var minX = vectorMap.keySet().stream().mapToInt(Vector2::x).min().orElseThrow();
        final var maxX = vectorMap.keySet().stream().mapToInt(Vector2::x).max().orElseThrow();
        final var maxY = vectorMap.keySet().stream().mapToInt(Vector2::y).max().orElseThrow();
        final var minY = vectorMap.keySet().stream().mapToInt(Vector2::y).min().orElseThrow();

        var width = maxX - minX + 1;
        var height = maxY - minY + 1;

        var buffer = new char[width * height];
        Arrays.fill(buffer, ' ');

        vectorMap.keySet().forEach(c -> {
            var colour = vectorMap.get(c);
            if (colour != Colour.BLACK) {
                buffer[((c.y() - minY) * width) + (c.x() - minX)] = '#';
            }
        });

        for (int row = 0; row < height; row++) {
            System.out.println(new String(buffer, row * width, width));
        }
    }


    private Map<Vector2, Colour> runPrintingRobot(Input input, Colour startColour) {
        final var computer = new Computer(Memory.init(input));
        final var visited = Maps.<Vector2, Colour>newHashMap();

        var position = new Vector2(0, 0);
        var direction = Direction.UP;

        visited.put(position, startColour);

        while (computer.running()) {
            computer.io().queueInput(visited.getOrDefault(position, Colour.BLACK).ordinal());

            if (computer.runUntilOutputAvailable()) {
                // first output is the colour to paint this position
                visited.put(position, Colour.values()[(int) computer.io().takeOutput()]);

                // second output is the direction to move
                computer.runUntilOutputAvailable();
                direction = computer.io().takeOutput() == 0 ?
                        direction.rotateLeft() :
                        direction.rotateRight();

                position = direction.move(position);
            }
        }

        return visited;
    }
}
